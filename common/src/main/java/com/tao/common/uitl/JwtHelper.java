package com.tao.common.uitl;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

public class JwtHelper {
    // 过期时间设置30分钟
    private static long jwtExpiration = System.currentTimeMillis() + 30 * 60 * 1000L;
    private static String jwtSignKey = "taozhu1@qq.com";

    /**
     * 生成JWT
     *
     * @param userId
     * @param userName
     * @param role
     * @return
     */
    public static String createToken(Integer userId, String userName, String role) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject("USER-AUTH")
                .claim("userId", userId)
                .claim("userName", userName)
                .claim("role", role)
                .signWith(SignatureAlgorithm.HS512, jwtSignKey) // 使用用户密码作为sign，修改密码后签名失效
                .compressWith(CompressionCodecs.GZIP);
//                .setExpiration(new Date(jwtExpiration));
        return jwtBuilder.compact();
    }



    /**
     * 检查token是否有效
     *
     * @param token
     * @return
     */
    public static Boolean checkJwt(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        try {
            Jwts.parser().setSigningKey(jwtSignKey).parseClaimsJws(token);
        } catch (Exception e) {
            // throw new RuntimeException("JWT解析异常！");
            return false;
        }
        return true;
    }

    /**
     * 解密jWT获取用户id
     *
     * @param token
     * @return
     */
    public static Claims decryptJwt(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Jws<Claims> claimsJws;
        try {
            claimsJws = Jwts.parser().setSigningKey(jwtSignKey).parseClaimsJws(token);
        } catch (Exception e) {
            return null;
        }
        // System.out.println(claimsJws);
        return claimsJws.getBody();
    }

    public static String parseToken(String token, String key) {
        Claims claims = decryptJwt(token);
        return String.valueOf(claims.get(key));
    }


    public static void main(String[] args) {
        String token = createToken(1, "taozhu", "admin");
        System.out.println(token);
        System.out.println(checkJwt(token));
        System.out.println(decryptJwt(token));
//        long exp = Long.valueOf(String.valueOf(parseToken(token, "exp"))) * 1000L;
//        System.out.println(exp);
//        System.out.println(new Date());
//        System.out.println(new Date(exp));
//        System.out.println(new Date(jwtExpiration));
    }
}

