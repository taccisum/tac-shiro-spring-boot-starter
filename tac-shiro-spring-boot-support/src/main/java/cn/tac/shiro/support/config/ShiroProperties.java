package cn.tac.shiro.support.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @author tac
 * @since 1.0
 */
@ConfigurationProperties(prefix = "shiro")
public class ShiroProperties {
    private Boolean enableRedirect = false;
    private RedirectUrlProperties redirectUrl = new RedirectUrlProperties();
    private String filterBasePackage;
    private Map<String, String> filters;
    private Map<String, List<String>> filterChainDefinition;
    private SessionProperties session = new SessionProperties();

    public Boolean getEnableRedirect() {
        return enableRedirect;
    }

    public void setEnableRedirect(Boolean enableRedirect) {
        this.enableRedirect = enableRedirect;
    }

    public RedirectUrlProperties getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(RedirectUrlProperties redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }

    public String getFilterBasePackage() {
        return filterBasePackage;
    }

    public void setFilterBasePackage(String filterBasePackage) {
        this.filterBasePackage = filterBasePackage;
    }

    public Map<String, List<String>> getFilterChainDefinition() {
        return filterChainDefinition;
    }

    public void setFilterChainDefinition(Map<String, List<String>> filterChainDefinition) {
        this.filterChainDefinition = filterChainDefinition;
    }

    public SessionProperties getSession() {
        return session;
    }

    public void setSession(SessionProperties session) {
        this.session = session;
    }

    public static class RedirectUrlProperties {
        private String login;
        private String success;
        private String unauthorized;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public String getUnauthorized() {
            return unauthorized;
        }

        public void setUnauthorized(String unauthorized) {
            this.unauthorized = unauthorized;
        }
    }

    public static class SessionProperties {
        private Long timeout = 30 * 60 * 1000L;

        public Long getTimeout() {
            return timeout;
        }

        public void setTimeout(Long timeout) {
            this.timeout = timeout;
        }
    }
}
