package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "获得STS临时访问token请求")
public class OssStsForm extends BaseForm {

    @ApiModelProperty(value = "配置参数名")
    private String paramsCode = "OSS_PRIVATE";

}
