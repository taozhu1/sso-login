package com.tao.ssoclient.filter;

import com.tao.common.uitl.JwtHelper;
import com.tao.common.uitl.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author: tao
 */
@Component
@Slf4j
public class UserTokenInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 提取token向sso认证中心验证token是否有效
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从header拿到jwt
        String token = request.getHeader("token");
        log.info("header域是否存在jwt:{}", token);
        if (StringUtils.isEmpty(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("http://127.0.0.1:9001/center/");
        }
        // 去SSO检查JWT是否有效 -> 换成去redis检查jwt是否存在
        // python这里可以换成去sso解密jwt
        String userId = JwtHelper.parseToken(token, "userId");
        String redisKey = RedisKeyUtil.getToken(userId);
        if (!redisTemplate.hasKey(redisKey)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("http://127.0.0.1:9001/center/");
        }

        // 过期续签
        if(redisTemplate.getExpire(redisKey) < 1 * 60 * 30){
            redisTemplate.opsForValue().set(redisKey, token, 2, TimeUnit.HOURS);
        }

        return true;
    }
}
