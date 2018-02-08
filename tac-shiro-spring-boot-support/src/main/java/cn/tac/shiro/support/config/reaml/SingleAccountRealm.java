package cn.tac.shiro.support.config.reaml;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Objects;

/**
 * 只有单个账号的realm
 *
 * @author tac
 * @since 1.0
 */
public class SingleAccountRealm extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken t = (UsernamePasswordToken) token;
        if (Objects.equals(t.getUsername(), "tac") && Objects.equals(t.getUsername(), "123456")) {
            return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        }
        throw new AuthenticationException("user name or password mismatch");
    }
}
