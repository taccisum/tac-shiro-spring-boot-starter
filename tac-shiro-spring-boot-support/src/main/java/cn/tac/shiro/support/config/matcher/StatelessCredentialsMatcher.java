package cn.tac.shiro.support.config.matcher;

import cn.tac.shiro.support.config.reaml.SimpleTokenRealm;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

/**
 * @author tac
 * @since 1.1
 */
public class StatelessCredentialsMatcher implements CredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        SimpleTokenRealm.SimpleTokenAuthenticationInfo i = (SimpleTokenRealm.SimpleTokenAuthenticationInfo) info;
        return i.isValid();
    }
}
