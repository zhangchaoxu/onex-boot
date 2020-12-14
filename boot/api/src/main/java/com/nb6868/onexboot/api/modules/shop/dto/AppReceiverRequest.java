package com.nb6868.onexboot.api.modules.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 微信公众号收货地址请求
 *
 * @author DHB
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "微信公众号收货地址请求")
public class AppReceiverRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "联络人", required = true)
    @NotBlank(message = "联络人不能为空")
    private String name;

    @ApiModelProperty(value = "联络电话", required = true)
    @NotBlank(message = "联络电话不能为空")
    private String tel;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "省份", required = true)
    @NotBlank(message = "省份信息不能为空")
    private String province;

    @ApiModelProperty(value = "市", required = true)
    @NotBlank(message = "市信息不能为空")
    private String city;

    @ApiModelProperty(value = "区", required = true)
    @NotBlank(message = "区信息不能为空")
    private String county;

    @ApiModelProperty(value = "区号ID", required = true)
    @NotBlank(message = "区号ID不能为空")
    private String areaCode;

    @ApiModelProperty(value = "邮政编码")
    private String postalCode;

    @ApiModelProperty(value = "详细地址", required = true)
    @NotBlank(message = "详细地址不能为空")
    private String addressDetail;

    @ApiModelProperty(value = "是否默认", required = true)
    @NotNull(message = "是否默认不能为空")
    private Integer defaultItem;

    @ApiModelProperty(value = "ID")
    private Long id;
}
