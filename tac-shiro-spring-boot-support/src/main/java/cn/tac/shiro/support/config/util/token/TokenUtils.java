package cn.tac.shiro.support.config.util.token;

import cn.tac.shiro.support.config.exception.TokenStorageNotFoundException;
import cn.tac.shiro.support.config.exception.TokenStrategyNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tac
 * @since 1.1
 */
public abstract class TokenUtils {
//    public static Storage storage;
    private static Map<String, Strategy> strategies = new HashMap<>();

//    public static void setStorage(Storage storage) {
//        TokenUtils.storage = storage;
//    }

    public static void registerStrategy(String key, Strategy strategy) {
        strategies.put(key, strategy);
    }

//    public static void store(String type, String hashKey, String token) {
//        checkTokenStorage();
//        storage.put(type, hashKey, token);
//    }
//
//    public static void get(String type, String hashKey, String token) {
//        checkTokenStorage();
//        storage.put(type, hashKey, token);
//    }
//
//    public static void remove(String type, String hashKey, String token) {
//        checkTokenStorage();
//        storage.put(type, hashKey, token);
//    }

    public static String create(String type, Object content) {
        checkTokenStrategy(type);
        return strategies.get(type).create(content);
    }

    public static Object verify(String type, String token) {
        checkTokenStrategy(type);
        return strategies.get(type).verify(token);
    }

//    private static void checkTokenStorage() {
//        if (storage == null) {
//            throw new TokenStorageNotFoundException();
//        }
//    }

    private static void checkTokenStrategy(String type) {
        if (!strategies.containsKey(type)) {
            throw new TokenStrategyNotFoundException(type);
        }
    }

//    public interface Storage {
//        void put(String key, String hashKey, String token);
//
//        String get(String key, String hashKey);
//
//        Long delete(String key, String hashKey);
//    }

    public interface Strategy {
        String create(Object arg);

        Object verify(String token);
    }
}
