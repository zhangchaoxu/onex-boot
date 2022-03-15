package com.nb6868.onex.common.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "基础分页请求")
public class PageForm extends BaseForm {

    @ApiModelProperty(value = "每页数")
    @Min(value = 1, message = "没页数不能小于0")
    private Long pageSize = 10L;

    @ApiModelProperty(value = "页码，从1开始")
    @Min(value = 1, message = "没页数不能小于0")
    private Long pageNo = 1L;

    @ApiModelProperty(value = "排序规则")
    private String sortFmt;

}
