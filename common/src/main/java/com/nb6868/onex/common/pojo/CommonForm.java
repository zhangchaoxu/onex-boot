package com.nb6868.onex.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "基础发送请求")
public class CommonForm extends BaseForm {

    /**
     * 单个校验
     */
    public interface OneGroup {}

    /**
     * 多个校验
     */
    public interface ListGroup { }

     @Schema(description = "ids")
    @NotEmpty(message = "{ids.require}", groups = ListGroup.class)
    private List<Long> ids;

     @Schema(description = "id")
    @NotNull(message = "{id.require}", groups = OneGroup.class)
    private Long id;

     @Schema(description = "备注")
    private String remark;

}
