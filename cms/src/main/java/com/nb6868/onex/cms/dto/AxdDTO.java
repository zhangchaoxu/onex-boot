package com.nb6868.onex.cms.dto;

import com.nb6868.onex.common.pojo.BaseTenantDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 广告位
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "广告位")
public class AxdDTO extends BaseTenantDTO {
    private static final long serialVersionUID = 1L;

     @Schema(description = "标题")
    private String name;

     @Schema(description = "位置")
    private String position;

     @Schema(description = "链接")
    private String link;

     @Schema(description = "备注")
    private String remark;

     @Schema(description = "图片")
    private String imgs;

     @Schema(description = "排序")
    private Integer sort;

     @Schema(description = "是否需要登录 0 不需要 1需要")
    private Integer needLogin;

}
