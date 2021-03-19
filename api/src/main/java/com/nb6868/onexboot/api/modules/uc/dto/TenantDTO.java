package com.nb6868.onexboot.api.modules.uc.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "租户")
public class TenantDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "有效期开始")
	private Date validStartTime;

	@ApiModelProperty(value = "有效期结束")
	private Date validEndTime;

	@ApiModelProperty(value = "状态 0 无效 1 有效")
	private Integer state;

}
