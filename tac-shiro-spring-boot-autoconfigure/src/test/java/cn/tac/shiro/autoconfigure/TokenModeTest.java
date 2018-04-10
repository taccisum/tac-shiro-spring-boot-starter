package cn.tac.shiro.autoconfigure;

import cn.tac.shiro.support.config.ShiroProperties;
import cn.tac.shiro.support.config.factory.StatelessDefaultSubjectFactory;
import cn.tac.shiro.support.config.filter.concrete.StatelessAjaxUserFilter;
import cn.tac.shiro.support.config.matcher.StatelessCredentialsMatcher;
import cn.tac.shiro.support.config.reaml.SimpleTokenRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.Filter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author tac
 * @since 1.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TokenModeTest.class)
@ImportAutoConfiguration(ShiroAutoConfiguration.class)
@ActiveProfiles("token-mode-test")
public class TokenModeTest {
    @Autowired
    private SecurityManager securityManager;
    @Autowired
    private ShiroProperties properties;
    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean;
    @Autowired
    private Realm realm;

    @Test
    public void testSimply() {
        assertThat(properties.getMode()).isEqualTo(ShiroProperties.AuthenticationMode.TOKEN);
        DefaultWebSecurityManager manager = (DefaultWebSecurityManager) securityManager;
        assertThat(manager.getSubjectFactory()).isInstanceOf(StatelessDefaultSubjectFactory.class);
        Filter filter = shiroFilterFactoryBean.getFilters().get("ajax_user");
        assertThat(filter).isNotNull();
        assertThat(filter).isInstanceOf(StatelessAjaxUserFilter.class);
        assertThat(realm).isNotNull();
        assertThat(realm).isInstanceOf(SimpleTokenRealm.class);
        assertThat(((SimpleTokenRealm) realm).getCredentialsMatcher()).isInstanceOf(StatelessCredentialsMatcher.class);
    }
}
