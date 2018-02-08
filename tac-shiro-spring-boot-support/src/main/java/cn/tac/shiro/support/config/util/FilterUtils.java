package cn.tac.shiro.support.config.util;

import javax.servlet.Filter;

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
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
