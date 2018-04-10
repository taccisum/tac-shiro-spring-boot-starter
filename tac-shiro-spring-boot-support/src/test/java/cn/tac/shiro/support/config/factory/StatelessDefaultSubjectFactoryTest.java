package cn.tac.shiro.support.config.factory;

import cn.tac.shiro.support.config.reaml.SimpleRealm;
import cn.tac.shiro.support.config.util.SecurityManagerHelper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author tac
 * @since 09/04/2018
 */
public class StatelessDefaultSubjectFactoryTest {
    @Test
    public void testGetSubject() throws Exception {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        SimpleRealm realm = new SimpleRealm();
        realm.addAccount("tac", "123456");
        manager.setRealm(realm);
        SecurityManagerHelper.disableSession(manager);
        SecurityUtils.setSecurityManager(manager);
        Subject subject = SecurityUtils.getSubject();
        assertThat(subject.isAuthenticated()).isFalse();
        assertThat(subject.getPrincipal()).isNull();
        subject.login(new UsernamePasswordToken("tac", "123456"));
        Subject subject1 = SecurityUtils.getSubject();
        assertThat(subject1.isAuthenticated()).isTrue();
        assertThat(subject1.getPrincipal()).isEqualTo("tac");
    }
}
