package cn.tac.shiro.support.config.util;

import cn.tac.shiro.support.config.ShiroProperties;

import javax.servlet.Filter;
import java.lang.reflect.Constructor;

/**
 * @author tac
 * @since 1.0
 */
public abstract class FilterUtils {
    public static Class classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Filter instanceForName(String className) {
        Class<Filter> filterClazz = classForName(className);
        try {
            return filterClazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Filter instanceForName(String className, ShiroProperties shiroProperties) {
        Class<Filter> filterClazz = classForName(className);
        try {
            Constructor<Filter> constructor;
            try {
                constructor = filterClazz.getConstructor(ShiroProperties.class);
            } catch (NoSuchMethodException e) {
                return filterClazz.newInstance();
            }
            return constructor.newInstance(shiroProperties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
