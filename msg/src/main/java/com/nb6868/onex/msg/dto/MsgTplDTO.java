package com.nb6868.onex.msg.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "消息-模板")
public class MsgTplDTO extends BaseDTO {

    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空", groups = DefaultGroup.class)
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型 1验证码 2状态通知 3营销消息")
    @NotNull(message = "类型不能为空", groups = DefaultGroup.class)
    private Integer type;

    @Schema(description = "渠道,短信sms/电邮email/微信公众号模板消息wx_mp_template/微信小程序订阅消息wx_ma_subscribe/站内信 notice")
    @NotBlank(message = "渠道不能为空", groups = DefaultGroup.class)
    private String channel;

    @Schema(description = "平台 如aliyun/juhe")
    private String platform;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "配置参数")
    private JSONObject params;

    @Schema(description = "租户编码")
    private String tenantCode;

}
