package com.tao.ssoclient.filter;

import com.tao.common.uitl.CookieUtil;
import com.tao.common.uitl.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: tao
 */
@Component
@Slf4j
public class UserTokenInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = CookieUtil.getCookieValue(request, "token");
        log.info("UserTokenInterceptor查询token{}", token);
        if (StringUtils.isEmpty(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("../center/index");
        }
        // 检查黑名单
        String redisKey = RedisKeyUtil.getUserTokenBlacklist(token);
        Boolean isBlackList = redisTemplate.opsForSet().isMember(redisKey, token);
        if (isBlackList) {
            log.info("token：{}在黑名单，无效", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("../center/index");
        }

        return true;
    }
}
