package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新状态请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@ApiModel(value = "修改状态请求")
public class ChangeStateRequest implements Serializable {

    /**
     * 状态只能取值0-1
     */
    public interface BoolStateGroup { }

    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空", groups = DefaultGroup.class)
    private Long id;

    @ApiModelProperty(value = "状态")
    @Range(min = 0, max = 1, message = "状态值取值0-1", groups = BoolStateGroup.class)
    private Integer state;

    @ApiModelProperty(value = "备注")
    private String remark;

}
