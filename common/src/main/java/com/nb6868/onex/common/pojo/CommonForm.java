package com.nb6868.onex.common.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "基础发送请求")
public class CommonForm implements Serializable {

    /**
     * 单个校验
     */
    public interface OneGroup {}

    /**
     * 多个校验
     */
    public interface ListGroup { }

    @ApiModelProperty(value = "ids")
    @NotEmpty(message = "{ids.require}", groups = ListGroup.class)
    private List<Long> ids;

    @ApiModelProperty(value = "id")
    @NotNull(message = "{id.require}", groups = OneGroup.class)
    private Long id;

    @ApiModelProperty(value = "备注")
    private String remark;

}
