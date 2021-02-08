package com.nb6868.onexboot.api.modules.uc.dingtalk;

import lombok.Data;

import java.io.Serializable;

/**
 * 企业内部应用的access_token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class AccessTokenResponse implements Serializable {

    /**
     * 返回码
     */
    private int errcode;
    /**
     * 返回描述
     */
    private String errmsg;
    /**
     * access_token的过期时间，单位秒
     */
    private int expires_in;
    /**
     * access_token
     */
    private String access_token;
    /**
     * 是否执行成功
     */
    public boolean isSuccess() {
        return errcode == 0;
    }

}
