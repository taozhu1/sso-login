package com.tao.common.vo;

import lombok.Data;

/**
 * @author: tao
 */
@Data
public class LoginVo {
    // 用户ID
    private int userId;
    private String password;
    // 回调地址。授权后回到相应的客户端页面
    // private String returnUrl;
}
