package cn.tac.shiro.autoconfigure;

import cn.tac.shiro.support.config.ShiroProperties;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import java.util.*;

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
    @Autowired
    private ShiroProperties shiroProperties;

    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);

        registerDefaultFilters(bean.getFilters());
        if (shiroProperties.getFilters() != null) {
            shiroProperties.getFilters().forEach((k, v) -> {
                String className = StringUtils.isEmpty(shiroProperties.getFilterBasePackage()) ? v : shiroProperties.getFilterBasePackage() + "." + v;
                Filter filter = instanceForName(className);
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
            chain.put("/**", "user");
        } else {
            chain.put("/**", "ajax_user");
        }

        bean.setFilterChainDefinitionMap(chain);
        return bean;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator bean = new DefaultAdvisorAutoProxyCreator();
        bean.setProxyTargetClass(true);
        return bean;
    }

    @Bean
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
            CacheManager cacheManager) {
        DefaultWebSecurityManager bean = new DefaultWebSecurityManager();
        bean.setRealm(realm);
        bean.setSessionManager(sessionManager);
        bean.setAuthenticator(authenticator);
        bean.setRememberMeManager(rememberMeManager);
        bean.setCacheManager(cacheManager);
        SecurityUtils.setSecurityManager(bean);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public Realm iniRealm(CredentialsMatcher credentialsMatcher) {
        IniRealm bean = new IniRealm("classpath:shiro-web.ini");
        bean.setCredentialsMatcher(credentialsMatcher);
        return bean;
    }

    /**
     * 默认的CredentialsMatcher，使用md5算法加密，迭代次数为5，一般需要通过重写bean来改变matcher行为
     */
    @Bean
    @ConditionalOnMissingBean
    public CredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher bean = new HashedCredentialsMatcher();
        bean.setHashAlgorithmName("MD5");
        bean.setHashIterations(5);
        bean.setStoredCredentialsHexEncoded(true);
        return bean;
    }

    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager bean = new DefaultWebSessionManager();
        bean.setGlobalSessionTimeout(shiroProperties.getSession().getTimeout());
        return bean;
    }

    @Bean
    public Authenticator authenticator(Realm realm) {
        ModularRealmAuthenticator bean = new ModularRealmAuthenticator();
        bean.setRealms(Arrays.asList(realm));
        //添加监听器将在spring的ContextRefreshedEvent中进行
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public Cookie cookie() {
        SimpleCookie bean = new SimpleCookie();
        bean.setName("REMEMBER_ME");
        bean.setHttpOnly(true);
        bean.setMaxAge(2592000);    //30天
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
        //todo::
        return null;
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

        //todo::
        List<AuthenticationListener> listeners = new ArrayList<>();
        listeners.add(new AuthenticationListener() {
            @Override
            public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
                logger.info("authenticated success");
            }

            @Override
            public void onFailure(AuthenticationToken token, AuthenticationException ae) {
                logger.info("authenticated failure");
                //do nothing
            }

            @Override
            public void onLogout(PrincipalCollection principals) {
                logger.info("log out");
                //do nothing
            }
        });
        authenticator.setAuthenticationListeners(listeners);
    }

    private void registerDefaultFilters(Map<String, Filter> filters) {
        logger.info("注册默认shiro filter");
        String ajaxUserFilterClassName = "cn.tac.shiro.support.config.filter.concrete.DefaultAjaxUserFilter";
        logger.debug("ajax_user: {}", ajaxUserFilterClassName);
        filters.put("ajax_user", instanceForName(ajaxUserFilterClassName));
    }
}
