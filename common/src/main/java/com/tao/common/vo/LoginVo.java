package com.tao.common.vo;

import lombok.Data;

/**
 * @author: tao
 */
@Data
public class LoginVo {
    private int userId;
    // 回调地址
    private String returnUrl;
}
