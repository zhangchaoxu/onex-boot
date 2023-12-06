package com.nb6868.onex.cms.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;

/**
 * 站点
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "站点")
public class SiteDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

    @Schema(description = "编码", required = true)
    @NotBlank(message = "{code.require}", groups = DefaultGroup.class)
    private String code;

    @Schema(description = "名称", required = true)
    @NotBlank(message = "{name.require}", groups = DefaultGroup.class)
    private String name;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String descr;

    @Schema(description = "网址")
    private String domain;

    @Schema(description = "LOGO")
    private String logo;

    @Schema(description = "版权信息")
    private String copyright;

    @Schema(description = "关键词")
    private String keywords;

    @Schema(description = "生成目录")
    private String dir;

    @Schema(description = "图片")
    private String imgs;

    @Schema(description = "状态", required = true)
    @Range(min = 0, max = 1, message = "状态取值0-1", groups = DefaultGroup.class)
    private Integer state;

}
