package cn.tac.shiro.autoconfigure;

import cn.tac.shiro.support.config.ShiroProperties;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author tac
 * @since 1.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShiroAutoConfigurationTest.class)
@ImportAutoConfiguration(ShiroAutoConfiguration.class)
public class ShiroAutoConfigurationTest {
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
        assertThat(properties).isNotNull();
        assertThat(shiroFilterFactoryBean).isNotNull();
        assertThat(securityManager).isNotNull();
        assertThat(securityManager).isInstanceOf(DefaultWebSecurityManager.class);
        assertThat(realm).isNotNull();
    }
}
