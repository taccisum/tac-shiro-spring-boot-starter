package cn.tac.shiro.support.config.exception;

/**
 * @author tac
 * @since 1.1
 */
public class TokenStrategyNotFoundException extends RuntimeException {
    public TokenStrategyNotFoundException(String type) {
        super(type);
    }
}
