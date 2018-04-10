package cn.tac.shiro.support.config.reaml;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单的realm
 *
 * @author tac
 * @since 1.0
 */
public class SimpleRealm extends AuthorizingRealm {
    private Map<String, String> accounts = new HashMap<>();

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken t = (UsernamePasswordToken) token;

        if (contains(t.getUsername())) {
            return new SimpleAuthenticationInfo(t.getUsername(), accounts.get(t.getUsername()), getName());
        }
        throw new AuthenticationException("user name or password mismatch");
    }

    public void addAccount(String username, String password) {
        accounts.putIfAbsent(username, password);
    }

    public boolean contains(String username) {
        return accounts.containsKey(username);
    }
}

