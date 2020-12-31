package com.nb6868.onexboot.api.modules.uc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 角色
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "角色")
public class RoleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "创建者")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long createId;

    @ApiModelProperty(value = "创建时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createTime;

    @ApiModelProperty(value = "更新者")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long updateId;

    @ApiModelProperty(value = "更新时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date updateTime;

    @ApiModelProperty(value = "删除标记")
    @JsonIgnore
    private Integer deleted;

    @ApiModelProperty(value = "编码")
    @NotBlank(message = "{code.require}", groups = DefaultGroup.class)
    private String id;

    @ApiModelProperty(value = "名称")
    @NotBlank(message = "{name.require}", groups = DefaultGroup.class)
    private String name;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "菜单ID列表")
    private List<Long> menuIdList;

    @ApiModelProperty(value = "部门ID列表")
    private List<Long> deptIdList;

}
