package cn.tac.shiro.support.config.filter.concrete;

import cn.tac.shiro.support.config.ShiroProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author tac
 * @since 09/04/2018
 */
public class StatelessAjaxUserFilterTest {
    private StatelessAjaxUserFilter filter;
    HttpServletRequest req;
    HttpServletResponse resp;
    @Rule
    public OutputCapture capture = new OutputCapture();

    @Before
    public void setUp() throws Exception {
        ShiroProperties properties = new ShiroProperties();
        properties.getToken().setHeader("token");
        properties.getToken().setType("test");

        filter = spy(new StatelessAjaxUserFilter4Test(properties));
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
    }

    @After
    public void tearDown() throws Exception {
        capture.reset();
    }

    @Test
    public void onAccessDeniedWhenIsLoginReq() throws Exception {
        doReturn(true).when(filter).isLoginReq(req, resp);
        assertThat(filter.onAccessDenied(req, resp)).isTrue();
    }

    @Test
    public void onAccessDeniedWhenTokenIsNull() throws Exception {
        doReturn(false).when(filter).isLoginReq(req, resp);
        when(req.getHeader("token")).thenReturn(null);
        assertThat(filter.onAccessDenied(req, resp)).isFalse();
        assertThat(capture.toString()).isEqualTo("需要先登录");
    }

    @Test
    public void onAccessDeniedWhenTokenIsNotNull() throws Exception {
        doReturn(false).when(filter).isLoginReq(req, resp);
        when(req.getHeader("token")).thenReturn("123456");
        assertThat(filter.onAccessDenied(req, resp)).isTrue();
        verify(filter, times(1)).doLogin("123456");
        assertThat(capture.toString()).isEqualTo("执行登录");
    }

    private class StatelessAjaxUserFilter4Test extends StatelessAjaxUserFilter {
        public StatelessAjaxUserFilter4Test(ShiroProperties properties) {
            super(properties);
        }

        @Override
        protected void writeJson(HttpServletResponse response, String body) throws IOException {
            System.out.print("需要先登录");
        }

        @Override
        protected void doLogin(String token) {
            System.out.print("执行登录");
        }
    }
}
