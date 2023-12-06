package com.nb6868.onex.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "行政区域")
public class RegionPcdt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "行政区编码")
    private Long adcode;

    @Schema(description = "层级深度")
    private Integer deep;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "乡镇/街道")
    private String township;

}
