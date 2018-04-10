package cn.tac.shiro.autoconfigure;

import cn.tac.shiro.support.config.ShiroProperties;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@SpringBootTest(classes = CacheEnableTest.class)
@ImportAutoConfiguration(ShiroAutoConfiguration.class)
@ActiveProfiles("cache-enable-test")
public class CacheEnableTest {
    @Autowired
    private SecurityManager securityManager;
    @Autowired
    private ShiroProperties properties;

    @Test
    public void testSimply() {
        assertThat(properties).isNotNull();
        assertThat(properties.getEnableCache()).isFalse();
        DefaultWebSecurityManager manager = (DefaultWebSecurityManager) securityManager;
        assertThat(manager.getCacheManager()).isNull();
    }
}
