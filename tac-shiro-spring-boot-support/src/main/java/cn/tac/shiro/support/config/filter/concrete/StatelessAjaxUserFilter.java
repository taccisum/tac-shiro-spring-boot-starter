package cn.tac.shiro.support.config.filter.concrete;

import cn.tac.shiro.support.config.ShiroProperties;
import cn.tac.shiro.support.config.filter.AjaxUserFilter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tac
 * @since 1.1
 */
public class StatelessAjaxUserFilter extends AjaxUserFilter {
    private ShiroProperties shiroProperties;

    public StatelessAjaxUserFilter(ShiroProperties shiroProperties) {
        this.shiroProperties = shiroProperties;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginReq(request, response)) {
            return true;
        } else {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String token = getToken(httpServletRequest);
            if (token == null) {
                writeJson((HttpServletResponse) response, responseBody());
                return false;
            } else {
                try {
                    doLogin(token);
                    return true;
                } catch (Exception e) {
                    //todo::
                    return false;
                }
            }
        }
    }

    private String getToken(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(shiroProperties.getToken().getHeader());
    }

    protected void doLogin(String token) {
        SecurityUtils.getSubject().login(new StatelessToken(token));
    }

    boolean isLoginReq(ServletRequest request, ServletResponse response) {
        return isLoginRequest(request, response);
    }

    public static class StatelessToken implements AuthenticationToken {
        private String token;

        public StatelessToken(String token) {
            this.token = token;
        }

        @Override
        public Object getPrincipal() {
            return token;
        }

        @Override
        public Object getCredentials() {
            return token;
        }

        public String getToken() {
            return token;
        }

        @Override
        public String toString() {
            return token;
        }
    }
}
