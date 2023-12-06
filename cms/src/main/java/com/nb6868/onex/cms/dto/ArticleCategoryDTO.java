package com.nb6868.onex.cms.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章分类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "文章类目")
public class ArticleCategoryDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;
    @Schema(description = "站点id")
    private Long siteId;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "图片")
    private String imgs;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态")
    private Integer state;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "备注")
    private String remark;

}
