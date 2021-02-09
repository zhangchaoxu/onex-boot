package com.nb6868.onexboot.api.modules.uc.dingtalk;

import lombok.Data;

import java.io.Serializable;

/**
 * 钉钉基础返回消息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class BaseResponse implements Serializable {

    /**
     * 返回码
     */
    private int errcode;
    /**
     * 返回描述
     */
    private String errmsg;

    /**
     * 是否执行成功
     */
    public boolean isSuccess() {
        return errcode == 0;
    }
}
