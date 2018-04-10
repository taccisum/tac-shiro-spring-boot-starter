package cn.tac.shiro.autoconfigure;

import cn.tac.shiro.support.config.ShiroProperties;
import cn.tac.shiro.support.config.reaml.SimpleRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author tac
 * @since 29/03/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SessionModeTest.class)
@ImportAutoConfiguration(ShiroAutoConfiguration.class)
@ActiveProfiles("session-mode-test")
public class SessionModeTest {
    @Autowired
    private SecurityManager securityManager;
    @Autowired
    private ShiroProperties properties;
    @Autowired
    private Realm realm;

    @Test
    public void testSimply() {
        assertThat(SecurityUtils.getSecurityManager()).isNotNull();
        assertThat(properties.getMode()).isEqualTo(ShiroProperties.AuthenticationMode.SESSION);
        assertThat(securityManager).isInstanceOf(DefaultWebSecurityManager.class);
        DefaultWebSecurityManager manager = (DefaultWebSecurityManager) securityManager;
        assertThat(manager.getSessionManager()).isNotNull();
        assertThat(manager.getRememberMeManager()).isNotNull();
        assertThat(manager.getAuthenticator()).isNotNull();
        assertThat(realm).isNotNull();
        assertThat(realm).isInstanceOf(SimpleRealm.class);
        assertThat(((SimpleRealm) realm).getCredentialsMatcher()).isInstanceOf(HashedCredentialsMatcher.class);
    }
}
