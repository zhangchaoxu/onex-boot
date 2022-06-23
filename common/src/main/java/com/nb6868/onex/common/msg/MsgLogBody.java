package com.nb6868.onex.common.msg;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class MsgLogBody implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 有效期结束
     */
    private Date validEndTime;

    /**
     * 内容参数
     */
    private JSONObject contentParams;

}
