package com.nb6868.onex.common.msg;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.TenantGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息发送请求")
public class MsgSendForm extends BaseForm {

    @ApiModelProperty(value = "租户编码", required = true)
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

    @ApiModelProperty(value = "模板编码", required = true, example = "CODE_LOGIN")
    @NotBlank(message = "模板编码不能为空", groups = DefaultGroup.class)
    private String tplCode;

    @ApiModelProperty(value = "收件人", required = true)
    @NotBlank(message = "收件人不能为空", groups = DefaultGroup.class)
    private String mailTo;

    @ApiModelProperty(value = "抄送人")
    private String mailCc;

    @ApiModelProperty(value = "标题参数")
    private JSONObject titleParams;

    @ApiModelProperty(value = "内容参数")
    private JSONObject contentParams;

    @ApiModelProperty(value = "附件")
    List<File> attachments;

}
