package com.tao.ssoclient.controller;

import com.tao.common.uitl.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: tao
 */
@Slf4j
@RestController
public class UserController {

    /**
     * http://127.0.0.1:8080/user/detail
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/detail")
    public Object jwtDetail(HttpServletRequest request) {
        // TODO 改成调接口
        String token = request.getHeader("token");
        log.info("获取token:{}", token);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return JwtHelper.decryptJwt(token);
    }
}
