package com.nb6868.onex.modules.ba.dto;

import com.nb6868.onex.booster.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秉奥-检测题
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "秉奥-检测题")
public class SubjectDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "题面")
	private String question;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "1 成人检测 2 孩子检测")
	private Integer type;

	@ApiModelProperty(value = "答案对应内容")
	private String answer;

	@ApiModelProperty(value = "答案选项")
	private String options;

	@ApiModelProperty(value = "答案解析")
	private String analysis;

	@ApiModelProperty(value = "状态0 未启用 1 启用")
	private Integer status;

}
