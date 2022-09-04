package com.tao.common.uitl;

/**
 * @author: tao
 */
public class RedisKeyUtil {

    private final static String USER_TOKEN = "TOKEN";
    private final static String USER_TOKEN_BLACKLIST = "TOKEN_BLACKLIST";
    private final static String SPILT = ":";

    public static String getToken(String token) {
        return USER_TOKEN + SPILT + token;
    }

    public static String getUserTokenBlacklist(String token) {
        return USER_TOKEN_BLACKLIST + SPILT + token;
    }
}
