package cn.tac.shiro.support.config.filter.concrete;

import cn.tac.shiro.support.config.filter.AjaxUserFilter;

/**
 * @author tac
 * @since 1.0
 */
public class DefaultAjaxUserFilter extends AjaxUserFilter {
    @Override
    protected String responseBody() {
        return "需要先登录";
    }
}
