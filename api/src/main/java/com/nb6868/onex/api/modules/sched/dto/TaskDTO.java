package com.nb6868.onex.api.modules.sched.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "定时任务")
public class TaskDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "名称")
    @NotBlank(message = "{name.require}", groups = DefaultGroup.class)
    private String name;

    @ApiModelProperty(value = "参数")
    private String params;

    @ApiModelProperty(value = "cron表达式")
    @NotBlank(message = "cron表达式不能为空", groups = DefaultGroup.class)
    private String cron;

    @ApiModelProperty(value = "状态  0：暂停  1：正常")
    @Range(min = 0, max = 1, message = "状态值错误", groups = DefaultGroup.class)
    private Integer state;

    @ApiModelProperty(value = "备注")
    private String remark;

}
