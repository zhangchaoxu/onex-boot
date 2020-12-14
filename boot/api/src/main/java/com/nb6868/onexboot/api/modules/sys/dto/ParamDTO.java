package com.nb6868.onexboot.api.modules.sys.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;

/**
 * 参数
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "参数")
public class ParamDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "参数编码")
    @NotBlank(message = "{code.require}", groups = DefaultGroup.class)
    private String code;

    @ApiModelProperty(value = "参数值")
    @NotBlank(message = "{content.require}", groups = DefaultGroup.class)
    private String content;

    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 将content转为json对象，便于前台解析
     */
    public Object getContentJson() {
        if (StringUtils.isNotBlank(content)) {
            try {
                return JacksonUtils.jsonToPojo(content, Object.class);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
