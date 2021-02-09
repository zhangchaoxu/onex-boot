package com.nb6868.onexboot.api.modules.uc.dingtalk;

import lombok.Data;

import java.io.Serializable;

/**
 * 根据unionid获取用户userid, 返回体
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class GetUserIdByUnionidResponse extends BaseResponse {

    /**
     * 用户信息
     */
    private Result result;

    @Data
    static class Result implements Serializable {
        /**
         * 联系类型：
         * 0：企业内部员工
         * 1：企业外部联系人
         */
        private int contact_type;
        /**
         * 用户的userid
         */
        private String userid;
    }

}
