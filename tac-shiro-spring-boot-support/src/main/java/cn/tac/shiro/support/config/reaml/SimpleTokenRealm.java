package cn.tac.shiro.support.config.reaml;

import cn.tac.shiro.support.config.ShiroProperties;
import cn.tac.shiro.support.config.filter.concrete.StatelessAjaxUserFilter;
import cn.tac.shiro.support.config.util.token.TokenUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * 简单的token realm
 *
 * @author tac
 * @since 1.1
 */
public class SimpleTokenRealm extends AuthorizingRealm {
    private ShiroProperties shiroProperties;

    public SimpleTokenRealm(ShiroProperties shiroProperties) {
        this.shiroProperties = shiroProperties;
    }

    @Override
    public Class getAuthenticationTokenClass() {
        return StatelessAjaxUserFilter.StatelessToken.class;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        StatelessAjaxUserFilter.StatelessToken t = (StatelessAjaxUserFilter.StatelessToken) token;
        return new SimpleJWTAuthenticationInfo(t, getName(), shiroProperties.getToken().getType());
    }

    public static abstract class SimpleTokenAuthenticationInfo extends SimpleAuthenticationInfo {
        private String type;
        private Object decodedInfo;

        public SimpleTokenAuthenticationInfo(StatelessAjaxUserFilter.StatelessToken t, String name, String type) {
            super(t, t.getToken(), name);
            this.type = type;
            this.decodedInfo = decoded(t, this.type);
        }

        protected abstract Object decoded(StatelessAjaxUserFilter.StatelessToken t, String type);

        public abstract boolean isValid();

        public Object getDecodedInfo() {
            return decodedInfo;
        }
    }

    public static class SimpleJWTAuthenticationInfo extends SimpleTokenAuthenticationInfo {

        public SimpleJWTAuthenticationInfo(StatelessAjaxUserFilter.StatelessToken t, String name, String type) {
            super(t, name, type);
        }

        @Override
        protected Object decoded(StatelessAjaxUserFilter.StatelessToken t, String type) {
            try {
                return TokenUtils.verify(type, t.getToken());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean isValid() {
            return getDecodedInfo() != null;
        }
    }
}
