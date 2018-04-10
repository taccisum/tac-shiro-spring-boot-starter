package cn.tac.shiro.support.config.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author tac
 * @since 1.1
 */
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Filter {
    @AliasFor("key")
    String value();

    @AliasFor("value")
    String key();
}
