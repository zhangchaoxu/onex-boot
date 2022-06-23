package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.TenantGroup;
import com.nb6868.onex.sys.MsgConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "验证码发送请求")
public class MsgCodeSendForm extends BaseForm {

    @ApiModelProperty(value = "租户编码", required = true)
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

    @ApiModelProperty(value = "模板编码", required = true, example = "CODE_LOGIN")
    @NotBlank(message = "模板编码不能为空", groups = DefaultGroup.class)
    private String tplCode = MsgConst.SMS_TPL_LOGIN;

    @ApiModelProperty(value = "收件人", required = true)
    @NotBlank(message = "收件人不能为空", groups = DefaultGroup.class)
    private String mailTo;

}
