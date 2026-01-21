package com.nb6868.onex.common.msg;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.CaptchaReq;
import com.nb6868.onex.common.pojo.FileBase64Req;
import com.nb6868.onex.common.validator.group.TenantGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "消息发送请求")
public class MsgSendReq extends CaptchaReq {

    @Schema(description = "租户编码")
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

    @Schema(description = "模板编码", example = "CODE_LOGIN")
    @NotEmpty(message = "模板编码不能为空")
    private String tplCode;

    @Schema(description = "收件人")
    @NotEmpty(message = "收件人不能为空")
    private String mailTo;

    @Schema(description = "抄送人")
    private String mailCc;

    @Schema(description = "标题参数")
    private JSONObject titleParams;

    @Schema(description = "内容参数")
    private JSONObject contentParams;

    @Schema(description = "附件")
    List<FileBase64Req> attachments;

    @Schema(description = "额外的参数")
    private JSONObject extParams;

}
