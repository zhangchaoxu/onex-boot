package com.nb6868.onexboot.api.modules.cms.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

/**
 * 站点
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "站点")
public class SiteDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编码", required = true)
    @NotBlank(message = "{code.require}", groups = DefaultGroup.class)
    private String code;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "{name.require}", groups = DefaultGroup.class)
    private String name;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String descr;

    @ApiModelProperty(value = "网址")
    private String domain;

    @ApiModelProperty(value = "LOGO")
    private String logo;

    @ApiModelProperty(value = "版权信息")
    private String copyright;

    @ApiModelProperty(value = "关键词")
    private String keywords;

    @ApiModelProperty(value = "图片")
    private String imgs;

    @ApiModelProperty(value = "状态", required = true)
    @Range(min = 0, max = 1, message = "状态取值0-1", groups = DefaultGroup.class)
    private Integer state;

}
