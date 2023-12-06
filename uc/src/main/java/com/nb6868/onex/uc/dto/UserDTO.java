package com.nb6868.onex.uc.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
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
	@NotBlank(message = "{username.require}", groups = DefaultGroup.class)
	private String username;

	 @Schema(description = "密码")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "{password.require}", groups = AddGroup.class)
	private String password;

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

	 @Schema(description = "部门链")
	private List<DeptDTO> deptChain;

	 @Schema(description = "角色名称")
	private String roleNames;

	 @Schema(description = "角色ID列表")
	private List<String> roleIds;

	 @Schema(description = "额外信息")
	private JSONObject extInfo;

	 @Schema(description = "第三方帐号信息")
	private JSONObject oauthInfo;

	 @Schema(description = "第三方帐号用户id")
	private String oauthUserid;

}
