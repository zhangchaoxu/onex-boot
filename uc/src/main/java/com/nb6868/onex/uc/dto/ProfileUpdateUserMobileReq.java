package com.nb6868.onex.uc.dto;

import cn.hutool.core.lang.RegexPool;
import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户个人参数更新请求")
public class ProfileUpdateUserMobileReq extends BaseReq {

    @Schema(description = "手机号")
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = RegexPool.MOBILE, message = "手机号码格式错误")
    private String mobile;

    @Schema(description = "短信验证码")
    @NotEmpty(message = "短信验证码不能为空")
    private String sms;

}
