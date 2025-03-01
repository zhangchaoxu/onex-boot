package com.nb6868.onex.uc.dto;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户")
public class UserDTO extends BaseDTO {

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "部门编码")
    private String deptCode;

    @Schema(description = "区域编码")
    private String areaCode;

    @Schema(description = "岗位编码")
    private String postCode;

    @Schema(description = "状态")
    private Integer state;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "等级")
    private String level;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "账户余额")
    private BigDecimal balance;

    @Schema(description = "积分")
    private BigDecimal points;

    @Schema(description = "收入余额")
    private BigDecimal income;

    @Schema(description = "租户编码")
    private String tenantCode;

    @Schema(description = "角色列表")
    private List<RoleRes> roleList;

    @Schema(description = "部门列表")
    private List<DeptRes> deptList;

    @Schema(description = "角色名称格式化")
    public String getRoleNameFmt() {
        return StrUtil.join(",", CollStreamUtil.toList(roleList, RoleRes::getName));
    }

    @Schema(description = "角色ID列表")
    public List<Long> getRoleIdList() {
        return CollStreamUtil.toList(roleList, RoleRes::getId);
    }

    @Schema(description = "部门名称格式化")
    public String getDeptNameFmt() {
        return StrUtil.join(",", CollStreamUtil.toList(deptList, DeptRes::getName));
    }

    @Schema(description = "部门ID列表")
    public List<Long> getDeptIdList() {
        return CollStreamUtil.toList(deptList, DeptRes::getId);
    }

    @Schema(description = "额外信息")
    private JSONObject extInfo;

    @Schema(description = "第三方帐号信息")
    private JSONObject oauthInfo;

    @Schema(description = "第三方帐号用户id")
    private String oauthUserid;

    @Schema(description = "关联表id")
    private Long relId;

    @Schema(description = "关联表名称")
    private String relName;

}
