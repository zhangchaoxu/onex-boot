package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "获得授权访问地址请求")
public class OssPresignedUrlForm extends BaseForm {

    @ApiModelProperty(value = "配置参数名")
    private String paramsCode = "OSS_PRIVATE";

    @ApiModelProperty(value = "文件key")
    @NotEmpty(message = "objectName不能为空")
    private String objectName;

    @ApiModelProperty(value = "过期描述")
    private Long expiration = 36000L;

}
