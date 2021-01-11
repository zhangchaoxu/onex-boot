package com.nb6868.onexboot.api.modules.msg.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "通知模板")
public class NoticeTplDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "编号")
	private String code;

	@ApiModelProperty(value = "模板名称")
	private String name;

	@ApiModelProperty(value = "配置")
	private String config;

	@ApiModelProperty(value = "参数")
	private String params;

	@ApiModelProperty(value = "短信内容")
	private String content;

}
