package com.nb6868.onex.uc.dto;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.nb6868.onex.common.jpa.Jackson2TypeHandler;
import com.nb6868.onex.common.pojo.BaseIdReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "部门")
public class DeptSaveOrUpdateReq extends BaseIdReq {

    @Schema(description = "上级ID，一级为0")
    @NotNull(message = "请选择上级")
    private Long pid;

    @Schema(description = "类型")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "编码")
    // @NotNull(message = "编码不能为空")
    private String code;

    @Schema(description = "上级编码")
    //@NotNull(message = "上级编码不能为空")
    private String pcode;

    @Schema(description = "区域编码")
    private String areaCode;

    @Schema(description = "部门名称")
    @NotBlank(message = "{name.require}")
    private String name;

    @Schema(description = "排序")
    @Min(value = 0, message = "{sort.number}")
    private Integer sort;

    @Schema(description = "第三方部门信息")
    @TableField(typeHandler = Jackson2TypeHandler.class)
    private JSONObject oauthInfo;

    @Schema(description = "第三方部门id")
    private String oauthDeptid;

    @Schema(description = "上级部门名称")
    private String parentName;

    @Schema(description = "租户编码")
    private String tenantCode;

}
