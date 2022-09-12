package com.tao.ssoclient.controller;

import com.tao.common.domain.User;
import com.tao.common.uitl.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author: tao
 */
@Slf4j
@RestController
public class UserController {

    static HashMap<Integer, User> userMap = new HashMap<>();

    static {
        User user1 = new User(1, "user1", "1111", "admin",0);
        User user2 = new User(2, "user2", "1111","user", 0);
        User user3 = new User(3, "user3", "1111","user", 0);
        userMap.put(1, user1);
        userMap.put(2, user2);
        userMap.put(3, user3);
    }


    /**
     * http://127.0.0.1:8080/user/detail
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/detail")
    public Object jwtDetail(HttpServletRequest request) {
        String token = request.getHeader("token");
        log.info("获取token:{}", token);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return JwtHelper.decryptJwt(token);
    }
}
