package com.nb6868.onex.common.msg;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class MsgTplBody implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    private String code;
    /**
     * 配置参数
     */
    private JSONObject params;

}
