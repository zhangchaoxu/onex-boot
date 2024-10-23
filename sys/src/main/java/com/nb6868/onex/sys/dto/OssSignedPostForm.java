package com.nb6868.onex.sys.dto;

import cn.hutool.json.JSONArray;
import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "获得授权post form请求")
public class OssSignedPostForm extends BaseForm {

    @Schema(description = "配置参数名")
    private String paramsCode = "OSS_PRIVATE";

    @Schema(description = "限制条件")
    private JSONArray conditions;

    @Schema(description = "过期秒数")
    private int expire = 3600;

}
