package com.nb6868.onexboot.api.modules.uc.dingtalk;

import lombok.Data;

/**
 * 企业内部应用的access_token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class AccessTokenResponse extends BaseResponse {

    /**
     * access_token的过期时间，单位秒
     */
    private int expires_in;
    /**
     * access_token
     */
    private String access_token;

}
