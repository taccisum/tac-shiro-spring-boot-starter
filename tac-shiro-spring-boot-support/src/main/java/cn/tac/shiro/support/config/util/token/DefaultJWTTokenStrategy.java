package cn.tac.shiro.support.config.util.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

/**
 * @author tac
 * @since 1.1
 */
public class DefaultJWTTokenStrategy implements TokenUtils.Strategy {
    private String type;
    private Algorithm algorithm = null;
    public static final int DEFAULT_EXPIRES_MINUTES = 30;

    {
        try {
            algorithm = Algorithm.HMAC256("secret");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultJWTTokenStrategy(String type) {
        this.type = type;
    }

    @Override
    public String create(Object arg) {
        TokenContent content = (TokenContent) arg;
        return JWT.create()
                .withIssuer(type)
                .withExpiresAt(calculateExpiresTime())
                .withClaim("userId", content.getUserId())
                .withClaim("userName", content.getUserName())
                .withJWTId(newJwtId())
                .sign(algorithm);
    }

    @Override
    public Object verify(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(type)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return new TokenContent(jwt.getClaim("userId").asLong(), jwt.getClaim("userName").asString());
    }

    private String newJwtId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static Date calculateExpiresTime() {
        return DateTime.now().plusMinutes(DEFAULT_EXPIRES_MINUTES).toDate();
    }

    public static class TokenContent {
        public TokenContent(Long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        private Long userId;
        private String userName;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
