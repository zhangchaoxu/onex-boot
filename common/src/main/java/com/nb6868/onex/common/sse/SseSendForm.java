package com.nb6868.onex.common.sse;

import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sse发送请求")
public class SseSendForm extends BaseForm {

    /**
     * 单点发送校验
     */
    public interface SendOneGroup {
    }

    /**
     * 多点发送校验
     */
    public interface SendMultiGroup {
    }

    @ApiModelProperty(value = "发送对象")
    @NotEmpty(message = "发送对象不能为空", groups = SendMultiGroup.class)
    private List<String> sidList;

    @ApiModelProperty(value = "发送对象")
    @NotEmpty(message = "发送对象不能为空", groups = SendOneGroup.class)
    private String sid;

    @ApiModelProperty(value = "发送内容", required = true)
    @NotNull(message = "发送内容不能为空", groups = DefaultGroup.class)
    private String content;

}
