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
    private AuthenticationMode mode = AuthenticationMode.SESSION;
    private Boolean enableRedirect = false;
    private RedirectUrlProperties redirectUrl = new RedirectUrlProperties();
    private String filterBasePackage;
    private Map<String, String> filters;
    private Map<String, List<String>> filterChainDefinition;
    private SessionProperties session = new SessionProperties();
    private Boolean enableCache = true;
    private HashedCredentialsMatcherProperties hashedCredentialsMatcher = new HashedCredentialsMatcherProperties();
    private CookieProperties cookie = new CookieProperties();
    private TokenProperties token = new TokenProperties();

    public AuthenticationMode getMode() {
        return mode;
    }

    public void setMode(AuthenticationMode mode) {
        this.mode = mode;
    }

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

    public Boolean getEnableCache() {
        return enableCache;
    }

    public void setEnableCache(Boolean enableCache) {
        this.enableCache = enableCache;
    }

    public HashedCredentialsMatcherProperties getHashedCredentialsMatcher() {
        return hashedCredentialsMatcher;
    }

    public void setHashedCredentialsMatcher(HashedCredentialsMatcherProperties hashedCredentialsMatcher) {
        this.hashedCredentialsMatcher = hashedCredentialsMatcher;
    }

    public CookieProperties getCookie() {
        return cookie;
    }

    public void setCookie(CookieProperties cookie) {
        this.cookie = cookie;
    }

    public TokenProperties getToken() {
        return token;
    }

    public void setToken(TokenProperties token) {
        this.token = token;
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

    public static class HashedCredentialsMatcherProperties {
        private String algorithmName = "MD5";
        private Integer iterations = 5;
        private Boolean storedHex = true;

        public String getAlgorithmName() {
            return algorithmName;
        }

        public void setAlgorithmName(String algorithmName) {
            this.algorithmName = algorithmName;
        }

        public Integer getIterations() {
            return iterations;
        }

        public void setIterations(Integer iterations) {
            this.iterations = iterations;
        }

        public Boolean getStoredHex() {
            return storedHex;
        }

        public void setStoredHex(Boolean storedHex) {
            this.storedHex = storedHex;
        }
    }

    public static class CookieProperties {
        private String name = "REMEMBER_ME";
        private Boolean httpOnly = true;
        private Integer maxAge = 2592000;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getHttpOnly() {
            return httpOnly;
        }

        public void setHttpOnly(Boolean httpOnly) {
            this.httpOnly = httpOnly;
        }

        public Integer getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(Integer maxAge) {
            this.maxAge = maxAge;
        }
    }

    public enum AuthenticationMode {
        SESSION,
        TOKEN
    }

    public static class TokenProperties {
        private String header = "token";
        private String type = "api_access";

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
