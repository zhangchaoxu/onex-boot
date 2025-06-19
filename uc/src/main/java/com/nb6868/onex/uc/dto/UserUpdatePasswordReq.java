package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseIdReq;
import com.nb6868.onex.common.pojo.IdReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "修改用户的密码请求")
public class UserUpdatePasswordReq extends IdReq {

    @Schema(description = "登录配置编码", example = "ADMIN_DINGTALK_CODE")
    @NotEmpty(message = "登录类型不能为空")
    private String type;

    @Schema(description = "新密码")
    @NotEmpty(message = "新密码不能为空")
    private String newPasswordEncrypted;

}
