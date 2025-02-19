package com.nb6868.onex.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Uuid查询请求")
public class UuidReq extends BaseReq {

    @Schema(description = "uuid")
    @NotBlank(message = "uuid参数不能为空")
    private String uuid;

}
