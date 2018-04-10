package cn.tac.shiro.support.config.util;

import cn.tac.shiro.support.config.util.token.TokenUtils;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author tac
 * @since 09/04/2018
 */
public class TokenUtilsTest {
    public static final String TYPE = "test";
    private TokenUtils.Strategy strategy;

    @Before
    public void setUp() throws Exception {
        strategy = mock(TokenUtils.Strategy.class);
        TokenUtils.registerStrategy(TYPE, strategy);
    }

    @Test
    public void testCreateAndVerify() {
        TestCreateTokenContent content = new TestCreateTokenContent("1", "tac");
        when(strategy.create(content)).thenReturn("123456");
        String token = TokenUtils.create(TYPE, content);
        assertThat(token).isNotBlank();
        assertThat(token).isEqualTo("123456");

        when(strategy.verify(token)).thenReturn(content);
        TestCreateTokenContent tokenInfo = (TestCreateTokenContent) TokenUtils.verify(TYPE, token);
        assertThat(tokenInfo).isNotNull();
        assertThat(tokenInfo.getUid()).isEqualTo("1");
        assertThat(tokenInfo.getUsername()).isEqualTo("tac");
    }

    static class TestCreateTokenContent {
        public TestCreateTokenContent(String uid, String username) {
            this.uid = uid;
            this.username = username;
        }

        private String uid;
        private String username;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
