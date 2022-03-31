package com.nb6868.onex.sched.dto;

import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TaskQueryForm extends PageForm {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "状态")
    private Integer state;

}
