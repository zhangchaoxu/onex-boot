package com.nb6868.onex.common.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "数据查询请求")
public class DbQueryForm extends DbForm {

    @Schema(description = "sql")
    @NotBlank(message = "sql不能为空")
    private String sql;

    @Schema(description = "sql参数")
    Map<String, Object> params;

}
