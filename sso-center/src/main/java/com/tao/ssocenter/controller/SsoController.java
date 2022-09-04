package com.tao.ssocenter.controller;

import com.tao.common.domain.User;
import com.tao.common.uitl.CookieUtil;
import com.tao.common.uitl.JwtHelper;
import com.tao.common.uitl.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author: tao
 */
@Slf4j
@Controller
public class SsoController {

    static HashMap<Integer, User> userMap = new HashMap<>();

    static {
        User user1 = new User(1, "user1", "1111", 0);
        User user2 = new User(2, "user2", "1111", 0);
        User user3 = new User(3, "user3", "1111", 0);
        userMap.put(1, user1);
        userMap.put(2, user2);
        userMap.put(3, user3);
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 实现逻辑的核心原理：前端请求Header中设置的token保持不变（作为redis的key），校验有效性以缓存中的token为准（redis的值）。
     *
     * @param userId      用户ID
     * @param redirectUrl 登录成功的地址
     * @param request
     * @param response
     * @return
     * http://127.0.0.1:9001/center/login?userId=1&redirectUrl=127.0.0.1:8080/user/detail
     * http://127.0.0.1:8080/user/detail
     */
    @GetMapping("/login")
    public String login(@RequestParam(name = "userId") Integer userId,
                        @RequestParam(name = "redirectUrl") String redirectUrl,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        // 判断用户名和密码必须不为空
        if (StringUtils.isEmpty(userId) || !userMap.containsKey(userId)) {
            // throw
            return "用户不存在！";
        }
        User user = userMap.get(userId);
        log.info("获取用户:{}", user);
        // 创建key
        String token = JwtHelper.createToken(user.getId(), user.getUserName(), user.getPassword());
        log.info("创建token:{}", token);

        // ！token续签：存储到redis，token作为全局key，value为临时token，30分钟后过期
        // 方案一，写一个拦截器，每次请求都续签30min -> 消耗较大
        // 方案二，把token过期时间设置到晚上，这样子redis也不需要了
        String redisKey = RedisKeyUtil.getToken(token);
        String tmpToken = JwtHelper.createToken(user.getId(), user.getUserName(), user.getPassword());
        redisTemplate.opsForValue().set(redisKey, tmpToken, 60, TimeUnit.MINUTES); // 时间为jwt过期时间的2倍

        // TODO 返回携带JWT的请求
        // 方案一，共享cookie，存在跨域请求问题
        CookieUtil.setCookie(request, response, "token", token);
        // 方案二，加入到header (前端vue判断是否存在token,如果存在的话,则每 /http header都加上token) -》体现jwt的无状态，无跨域问题
        // 方案三，请求头URL携带参数 "?token=" + token;
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/check")
    @ResponseBody
    public User checkJwt(HttpServletRequest request) {
        String token = CookieUtil.getCookieValue(request, "token");
        log.info("检查token:{}", token);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Integer userId = JwtHelper.getUserId(token);
        User user = userMap.get(userId);
        log.info("获取用户:{}", user);
        return user;
    }

    @PostMapping("/refreshJwt")
    @ResponseBody
    public String refreshJwt() {
        return null;
    }

    /**
     * 修改密码、注销拉到黑名单，时间设置JWT的2倍
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    @ResponseBody
    public String logout(HttpServletRequest request) {
        String token = CookieUtil.getCookieValue(request, "token");
        log.info("检查token:{}", token);
        if (StringUtils.isEmpty(token)) {
            return "您未登录！";
        }
        // JWT加入黑名单
        String redisKey = RedisKeyUtil.getUserTokenBlacklist(token);
        redisTemplate.opsForSet().add(redisKey, token);
        return "注销成功！";
    }
}
