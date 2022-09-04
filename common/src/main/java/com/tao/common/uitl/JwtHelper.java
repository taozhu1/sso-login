package com.tao.common.uitl;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

public class JwtHelper {
    // 过期时间设置30分钟
    private static long tokenExpiration = System.currentTimeMillis() + 30 * 60 * 1000;
    private static String tokenSignKey = "11111111111";

    public static String createToken(Integer userId, String userName, String password) {
        // tokenSignKey = MD5Util.encrypt(password);
        String token = Jwts.builder()
                .setSubject("SSO-AUTH")
                .setExpiration(new Date(tokenExpiration))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey) // 使用用户密码作为sign，修改密码后签名失效
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static Integer getUserId(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        } catch (Exception e) {
            // throw new RuntimeException("JWT解析异常！");
            return null;
        }
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer) claims.get("userId");
        return userId.intValue();
    }

    public static String getUserName(String token) {
        if (StringUtils.isEmpty(token)) {
            return "";
        }
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("userName");
    }

    public static void main(String[] args) {
        String token = createToken(1, "zt", "1111");
        System.out.println(getUserId(token));
        System.out.println(getUserName(token));
    }
}

