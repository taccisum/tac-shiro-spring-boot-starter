package cn.tac.shiro.support.config.reaml;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author tac
 * @since 09/04/2018
 */
public class SimpleRealmTest {
    @Test
    public void testSimply() {
        SimpleRealm realm = new SimpleRealm();
        realm.addAccount("tac", "123456");
        realm.addAccount("anit", "abcde");
        assertThat(realm.contains("tac"));
        assertThat(realm.contains("anit"));
    }

    @Test
    public void doGetAuthorizationInfo() throws Exception {
        SimpleRealm realm = new SimpleRealm();
        realm.addAccount("tac", "123456");

        AuthenticationInfo info1 = realm.doGetAuthenticationInfo(new UsernamePasswordToken("tac", "123456"));
        assertThat(info1).isNotNull();
        assertThat(info1.getCredentials()).isEqualTo("123456");
        //---
        AuthenticationInfo info2 = realm.doGetAuthenticationInfo(new UsernamePasswordToken("tac", "1234567"));
        assertThat(info2).isNotNull();
        assertThat(info2.getCredentials()).isEqualTo("123456");
        //---
        try {
            realm.doGetAuthenticationInfo(new UsernamePasswordToken("tac1", "123456"));
            Assert.fail();
        } catch (AuthenticationException ignored) {
        }
    }

    @Test
    public void doGetAuthenticationInfo() throws Exception {
    }
}
