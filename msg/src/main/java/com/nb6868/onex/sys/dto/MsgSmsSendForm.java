package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.sys.MsgConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "短信发送请求")
public class MsgSmsSendForm extends BaseForm {

    @ApiModelProperty(value = "模板编码", required = true, example = "CODE_LOGIN")
    @NotBlank(message = "模板编码不能为空", groups = Default.class)
    private String tplCode = MsgConst.SMS_TPL_LOGIN;

    @ApiModelProperty(value = "收件人", required = true)
    @NotBlank(message = "收件人不能为空", groups = Default.class)
    private String mailTo;

}
