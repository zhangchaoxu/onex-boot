package com.nb6868.onex.modules.ba.dto;

import com.nb6868.onex.booster.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秉奥-用户检测细项
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "秉奥-用户检测细项")
public class ExamItemDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户id")
	private Long userId;

	@ApiModelProperty(value = "检测id")
	private Long testId;

	@ApiModelProperty(value = "题面")
	private String subjectQuestion;

	@ApiModelProperty(value = "选中结果")
	private String subjectOption;

	@ApiModelProperty(value = "选中结果答案")
	private String subjectAnswer;

}
