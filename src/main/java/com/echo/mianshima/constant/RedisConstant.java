package com.echo.mianshima.constant;

public class RedisConstant {
    public static final String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signins";

    public static String getUserSignInRedisKey(Integer year, Long userId){
        return USER_SIGN_IN_REDIS_KEY_PREFIX + ":" + year.toString() + ":" + userId.toString();
    }
}
