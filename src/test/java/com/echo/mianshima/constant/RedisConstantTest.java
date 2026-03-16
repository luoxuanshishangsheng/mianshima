package com.echo.mianshima.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedisConstantTest {
    @Test
    void testGetRedisKey(){
        String userSignInRedisKey = RedisConstant.getUserSignInRedisKey(2015, 3L);
        System.out.println(userSignInRedisKey);
    }
}