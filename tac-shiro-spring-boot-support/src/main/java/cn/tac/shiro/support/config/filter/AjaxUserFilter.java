package cn.tac.shiro.support.config.filter;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用于ajax请求的user filter，当前用户未认证时将返回json数据
 *
 * @author tac
 * @since 1.0
 */
public abstract class AjaxUserFilter extends UserFilter {
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        saveRequest(request);
        writeJson((HttpServletResponse) response, responseBody(), responseHttpStatus());
        return false;
    }

    protected HttpStatus responseHttpStatus() {
        return HttpStatus.OK;
    }

    protected void writeJson(HttpServletResponse response, String body, HttpStatus status) throws IOException {
        writeJson(response, body, status, "UTF-8");
    }

    protected void writeJson(HttpServletResponse response, String body, HttpStatus status, String encoding) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(encoding);
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.getWriter().write(body);
    }

    protected abstract String responseBody();
}
