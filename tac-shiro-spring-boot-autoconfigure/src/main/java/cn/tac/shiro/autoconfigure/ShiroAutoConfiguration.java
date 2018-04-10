package cn.tac.shiro.autoconfigure;

import cn.tac.shiro.autoconfigure.exception.AutoConfigureShiroException;
import cn.tac.shiro.support.config.ShiroProperties;
import cn.tac.shiro.support.config.matcher.StatelessCredentialsMatcher;
import cn.tac.shiro.support.config.reaml.SimpleRealm;
import cn.tac.shiro.support.config.reaml.SimpleTokenRealm;
import cn.tac.shiro.support.config.util.SecurityManagerHelper;
import cn.tac.shiro.support.config.util.token.DefaultJWTTokenStrategy;
import cn.tac.shiro.support.config.util.token.TokenUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.tac.shiro.support.config.util.FilterUtils.instanceForName;

/**
 * @author tac
 * @since 1.0
 */
@Configuration
@ConditionalOnClass(ShiroProperties.class)
@EnableConfigurationProperties(ShiroProperties.class)
public class ShiroAutoConfiguration {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroProperties shiroProperties) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);

        registerDefaultFilters(bean.getFilters(), shiroProperties);

        if (shiroProperties.getFilters() != null) {
            shiroProperties.getFilters().forEach((k, v) -> {
                String className = StringUtils.isEmpty(shiroProperties.getFilterBasePackage()) ? v : shiroProperties.getFilterBasePackage() + "." + v;
                Filter filter = instanceForName(className, shiroProperties);
                bean.getFilters().put(k, filter);
                logger.info("注册类{}为shiro filter", filter.getClass());
            });
        }

        if (shiroProperties.getEnableRedirect()) {
            bean.setLoginUrl(shiroProperties.getRedirectUrl().getLogin());
            bean.setSuccessUrl(shiroProperties.getRedirectUrl().getSuccess());
            bean.setUnauthorizedUrl(shiroProperties.getRedirectUrl().getUnauthorized());
        }

        Map<String, String> chain = new LinkedHashMap<>();

        if (shiroProperties.getFilterChainDefinition() != null) {
            shiroProperties.getFilterChainDefinition().forEach((filter, urls) -> {
                for (String url : urls) {
                    chain.put(url, filter);
                }
            });
        }

        if (shiroProperties.getEnableRedirect()) {
            chain.putIfAbsent("/**", "user");
        } else {
            chain.putIfAbsent("/**", "ajax_user");
        }

        bean.setFilterChainDefinitionMap(chain);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator bean = new DefaultAdvisorAutoProxyCreator();
        bean.setProxyTargetClass(true);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityManager securityManager(
            Realm realm,
            SessionManager sessionManager,
            Authenticator authenticator,
            RememberMeManager rememberMeManager,
            CacheManager cacheManager,
            ShiroProperties shiroProperties) {
        DefaultWebSecurityManager bean = new DefaultWebSecurityManager();

        if (Objects.equals(ShiroProperties.AuthenticationMode.SESSION, shiroProperties.getMode())) {
            logger.info("Shiro当前运行在SESSION模式");
            bean.setSessionManager(sessionManager);
            bean.setRememberMeManager(rememberMeManager);
        } else if (Objects.equals(ShiroProperties.AuthenticationMode.TOKEN, shiroProperties.getMode())) {
            logger.info("Shiro当前运行在TOKEN模式，所有与Session相关的操作将被禁止");
            SecurityManagerHelper.disableSession(bean);
        } else {
            throw new AutoConfigureShiroException(String.format("暂不支持的认证模式：%s", shiroProperties.getMode().name()));
        }

        bean.setRealm(realm);
        bean.setAuthenticator(authenticator);
        if (shiroProperties.getEnableCache()) {
            bean.setCacheManager(cacheManager);
        }
        SecurityUtils.setSecurityManager(bean);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public Realm realm(CredentialsMatcher credentialsMatcher, ShiroProperties shiroProperties) {
        AuthorizingRealm bean;
        if (Objects.equals(shiroProperties.getMode(), ShiroProperties.AuthenticationMode.SESSION)) {
            if (new ClassPathResource("shiro-web.ini").exists()) {
                bean = new IniRealm("classpath:shiro-web.ini");
            } else {
                logger.warn("classpath:shiro-web.ini未找到，将使用{}代替", SimpleRealm.class);
                SimpleRealm tmp = new SimpleRealm();
                tmp.addAccount("admin", "123456");
                bean = tmp;
            }
        } else {
            SimpleTokenRealm realm = new SimpleTokenRealm(shiroProperties);
            TokenUtils.registerStrategy(shiroProperties.getToken().getType(), new DefaultJWTTokenStrategy(shiroProperties.getToken().getType()));
            bean = realm;
        }
        bean.setCredentialsMatcher(credentialsMatcher);
        return bean;
    }

    /**
     * 默认的CredentialsMatcher，使用md5算法加密，迭代次数为5，一般需要通过重写bean来改变matcher行为
     */
    @Bean
    @ConditionalOnMissingBean
    public CredentialsMatcher credentialsMatcher(ShiroProperties shiroProperties) {
        CredentialsMatcher bean = null;
        if (Objects.equals(shiroProperties.getMode(), ShiroProperties.AuthenticationMode.SESSION)) {
            HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
            matcher.setHashAlgorithmName(shiroProperties.getHashedCredentialsMatcher().getAlgorithmName());
            matcher.setHashIterations(shiroProperties.getHashedCredentialsMatcher().getIterations());
            matcher.setStoredCredentialsHexEncoded(shiroProperties.getHashedCredentialsMatcher().getStoredHex());
            bean = matcher;
        } else {
            StatelessCredentialsMatcher matcher = new StatelessCredentialsMatcher();
            bean = matcher;
        }
        return bean;
    }

    @Bean
//    todo:: 不能使用该注解
//    @ConditionalOnMissingBean
    public SessionManager sessionManager(ShiroProperties shiroProperties) {
        DefaultWebSessionManager bean = new DefaultWebSessionManager();
        bean.setGlobalSessionTimeout(shiroProperties.getSession().getTimeout());
        return bean;
    }

    @Bean
//    @ConditionalOnMissingBean
    public Authenticator authenticator(Realm realm) {
        ModularRealmAuthenticator bean = new ModularRealmAuthenticator();
        bean.setRealms(Arrays.asList(realm));
        //添加监听器将在spring的ContextRefreshedEvent中进行
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public Cookie cookie(ShiroProperties shiroProperties) {
        SimpleCookie bean = new SimpleCookie();
        bean.setName(shiroProperties.getCookie().getName());
        bean.setHttpOnly(shiroProperties.getCookie().getHttpOnly());
        bean.setMaxAge(shiroProperties.getCookie().getMaxAge());
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public RememberMeManager rememberMeManager(Cookie cookie) {
        CookieRememberMeManager bean = new CookieRememberMeManager();
        bean.setCookie(cookie);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    /**
     * ![important]
     * 此处使用spring的事件监听来为shiro添加监听器，是为了避免shiro对业务bean的依赖
     * 导致相关bean的加载时机过早而无法被代理，进而引起如`注解式事务`之类由spring通过
     * 代理实现的功能无效化的问题
     */
    @org.springframework.context.event.EventListener
    public void onContextInitOrRefresh(ContextRefreshedEvent event) {
        ModularRealmAuthenticator authenticator = event.getApplicationContext().getBean(ModularRealmAuthenticator.class);
        List<AuthenticationListener> listeners = event.getApplicationContext().getBeansOfType(AuthenticationListener.class)
                .values().stream().collect(Collectors.toList());
        authenticator.setAuthenticationListeners(listeners);
    }

    private void registerDefaultFilters(Map<String, Filter> filters, ShiroProperties shiroProperties) {
        logger.info("为Shiro注册默认Filter");
        String filterClassName;
        if (Objects.equals(shiroProperties.getMode(), ShiroProperties.AuthenticationMode.SESSION)) {
            filterClassName = "cn.tac.shiro.support.config.filter.concrete.DefaultAjaxUserFilter";
            filters.put("ajax_user", instanceForName(filterClassName));
        } else {
            filterClassName = "cn.tac.shiro.support.config.filter.concrete.StatelessAjaxUserFilter";
            filters.put("ajax_user", instanceForName(filterClassName, shiroProperties));
        }
        logger.debug("ajax_user: {}", filterClassName);
    }
}
