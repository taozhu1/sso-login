package com.tao.ssocenter.controller;

import com.tao.common.domain.User;
import com.tao.common.uitl.JwtHelper;
import com.tao.common.uitl.RedisKeyUtil;
import com.tao.common.uitl.Result;
import com.tao.common.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
        User user1 = new User(1, "user1", "1111", "admin", 0);
        User user2 = new User(2, "user2", "1111", "user", 0);
        User user3 = new User(3, "user3", "1111", "user", 0);
        userMap.put(1, user1);
        userMap.put(2, user2);
        userMap.put(3, user3);
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * SSO登陆接口
     * <p>
     * TODO JWT续签方案
     * 方案1：每次请求都返回新的Token，这种简单粗暴，不存在续签的问题，不过相信很多人不会用，请求量大的话性能损耗也是比较明显。
     * 方案2：JWT 有效期设置到半夜。
     * 方案3：生成的JWT，不加入过期时间，在服务端Redis额外存储一个对应的过期时间，并每次操作延期。这种设计感觉很多余，既然保存到了Redis，JWT从无状态变成了有状态，既然能够保存过期时间，为啥不把用户信息都保存到Redis中，何必用JWT加密后前后端传来传去没有意义。
     * 方案4：每次登录的时候生成两个token给前端进行返回，一个是用于鉴别用户身份的token，另外一个token则是用于刷新token用的（jwt生成token，token放入redis中，accessToken过期短，refreshToken过期长）。
     * 方案5：临近过期刷新JWT，返回新的Token，很多人也采用的是这种方案。
     * TODO 方案5优化：jwt不设置过期时间，是否有效则通过redis控制（虽然违法了无状态，但续签、黑名单得到解决）
     * <p>
     * TODO 返回携带JWT的请求，交给前端处理
     * 方案一，存储到cookie，存在跨域请求问题
     * 方案二（推荐），前端拦截器，加入到header
     * 方案三，参数携带携带参数 "?token=" + token
     *
     * @return http://127.0.0.1:9001/center/login?userId=1&redirectUrl=127.0.0.1:8080/user/detail -> 返回到cookie，前端存到header域
     * 其他APP调用 http://127.0.0.1:8080/user/detail
     */
    @GetMapping("/login")
    @ResponseBody
    public Result login(LoginVo loginVo) {
        // 校验参数
        if (StringUtils.isEmpty(loginVo.getUserId())) {
            throw new IllegalArgumentException("参数异常");
        }
        User user = userMap.get(loginVo.getUserId());
        if (null == user) {
            throw new IllegalArgumentException("用户不存在");
        }
        log.info("获取到用户信息 -> {}", user);

        // 用户存在，生成token，存入redis
        String token = JwtHelper.createToken(user.getId(), user.getUserName(), user.getRole());
        String redisKey = RedisKeyUtil.getToken(String.valueOf(loginVo.getUserId()));
        redisTemplate.opsForValue().set(redisKey, token, 2, TimeUnit.HOURS); // init 2h
        log.info("创建jwt -> {}，存储到 redis，key{}", token, redisKey);
        return Result.ok(token);
    }

    /**
     * 检查jwt是否有效
     *
     * @param token
     * @return
     */
    @GetMapping("/checkJwt")
    @ResponseBody
    public Result checkJwt(@RequestParam("token") String token) {
        Boolean isValid = JwtHelper.checkJwt(token);
        // 改成通过redis判断
        String userId = JwtHelper.parseToken(token, "userId");
        String redisKey = RedisKeyUtil.getToken(userId);
        Boolean isExist = redisTemplate.hasKey(redisKey);
        if (StringUtils.isEmpty(token) || !isValid || !isExist) {
            return Result.fail("无效jwt");
        }
        // 检查过期时间，是否续签 -> 放到了redis中处理
        return Result.ok(token);
    }

    /**
     * 刷新jwt
     *
     * @param token
     * @return
     */
    @PostMapping("/refreshJwt")
    @ResponseBody
    public Result refreshJwt(@RequestParam(name = "token") String token) {
        String redisKey = RedisKeyUtil.getToken(token);
        redisTemplate.opsForValue().set(redisKey, token, 2, TimeUnit.HOURS); // init 2h
        log.info("refresh token {}", token);
        return Result.ok(token);
    }

    /**
     * 注销
     *
     * @param token
     * @return
     */
    @GetMapping("/logout")
    @ResponseBody
    public Result logout(@RequestParam("token") String token) {
        if (StringUtils.isEmpty(token)) {
            return Result.fail("您未登录!");
        }
        // 删除redisKey
        String redisKey = RedisKeyUtil.getToken(token);
        redisTemplate.delete(redisKey);
        return Result.ok("注销成功:" + token);
    }


    @GetMapping("/")
    public ModelAndView index(ModelAndView mv) {
        mv.setViewName("index");
        return mv;
    }
}
