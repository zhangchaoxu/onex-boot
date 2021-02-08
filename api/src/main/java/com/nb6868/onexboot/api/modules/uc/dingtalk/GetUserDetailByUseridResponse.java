package com.nb6868.onexboot.api.modules.uc.dingtalk;

import lombok.Data;

import java.io.Serializable;

/**
 * 根据userid获取用户详情, 返回体
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class GetUserDetailByUseridResponse implements Serializable {

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
    private Result result;

    /**
     * 是否执行成功
     */
    public boolean isSuccess() {
        return errcode == 0;
    }

    @Data
    static class Result implements Serializable {
        /**
         * 唯一id
         */
        private String unionid;
        /**
         * 用户的userid
         */
        private String userid;
        /**
         * 名称
         */
        private String name;
        /**
         * 头像
         */
        private String avatar;
        /**
         * 手机号
         */
        private String mobile;
        /**
         * 工号
         */
        private String job_number;
        /**
         * 邮箱
         */
        private String email;
    }

}
