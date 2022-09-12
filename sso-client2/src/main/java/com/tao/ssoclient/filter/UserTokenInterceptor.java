package com.tao.ssoclient.filter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tao.common.uitl.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 提取token向sso认证中心验证token是否有效
     *
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
            response.sendRedirect("http://127.0.0.1:3001/center/");
        }

        // 去SSO授权中心检查JWT是否有效，解密userId
        String res = OkHttpUtil.get("http://127.0.0.1:3001/center/checkJwt?token=" + token, null);
        log.info("checkJwt return {}", res);
        JsonElement je = new JsonParser().parse(res);
        if (!je.getAsJsonObject().get("code").toString().equals("200")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("http://127.0.0.1:3001/center/");
        }

        return true;
    }
}
