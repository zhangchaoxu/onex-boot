package com.nb6868.onexboot.api.modules.uc.dingtalk;

import lombok.Data;

import java.io.Serializable;

/**
 * 通过code获取用户信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class GetUserInfoByCodeResponse implements Serializable {

    /**
     * 返回码
     */
    private int errcode;
    /**
     * 返回描述
     */
    private String errmsg;
    /**
     * 用户信息
     */
    private UserInfo user_info;

    static class UserInfo implements Serializable {
        /**
         * 用户在钉钉上面的昵称
         */
        private String nick;
        /**
         * 用户在当前开放应用所属企业的唯一标识
         */
        private String unionid;
        /**
         * 用户在当前开放应用内的唯一标识
         */
        private String openid;
        /**
         * 	用户主企业是否达到高级认证级别
         */
        private Boolean main_org_auth_high_level;
    }

}
