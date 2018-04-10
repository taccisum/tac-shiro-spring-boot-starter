package cn.tac.shiro.support.config.util.token;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author tac
 * @since 10/04/2018
 */
public class DefaultJWTTokenStrategyTest {
    @Test
    public void testCreateAndVerify() throws Exception {
        DefaultJWTTokenStrategy strategy = new DefaultJWTTokenStrategy("api_access");
        String token = strategy.create(new DefaultJWTTokenStrategy.TokenContent(1L, "taccisum"));
        System.out.println(token);
        DefaultJWTTokenStrategy.TokenContent info = (DefaultJWTTokenStrategy.TokenContent) strategy.verify(token);
        assertThat(info.getUserId()).isEqualTo(1L);
        assertThat(info.getUserName()).isEqualTo("taccisum");
    }

    @Test(expected = TokenExpiredException.class)
    public void testExpire() {
        DefaultJWTTokenStrategy strategy = new DefaultJWTTokenStrategy("test");
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ0ZXN0IiwiZXhwIjoxNTIzMzQxMzk3LCJ1c2VyTmFtZSI6InRhY2Npc3VtIiwidXNlcklkIjoxLCJqdGkiOiJjYTI1OTUwODYzZTc0ZmVhOTUzODFkMzM0NjRmZGQwNyJ9.6TyKcRroZ46GCRkE8jx7DZfI_BZdeF0uRS0f6LgKSwY";
//        System.out.println(strategy.create(new DefaultJWTTokenStrategy.TokenContent(1L, "taccisum")));
        strategy.verify(token);
    }

    @Test(expected = JWTDecodeException.class)
    public void testVerifyFail() {
        DefaultJWTTokenStrategy strategy = new DefaultJWTTokenStrategy("test");
        String token = "123456";
        strategy.verify(token);
    }
}
