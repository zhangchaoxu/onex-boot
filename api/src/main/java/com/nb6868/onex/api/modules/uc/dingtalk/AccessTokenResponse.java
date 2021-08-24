package com.nb6868.onex.api.modules.uc.dingtalk;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业内部应用的access_token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
