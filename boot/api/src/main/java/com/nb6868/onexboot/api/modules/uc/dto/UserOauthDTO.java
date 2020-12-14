package com.nb6868.onexboot.api.modules.uc.dto;

import com.nb6868.onexboot.common.pojo.BaseTenantDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "第三方用户")
public class UserOauthDTO extends BaseTenantDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "appid")
	private String appid;

	@ApiModelProperty(value = "unionid")
	private String unionid;

	@ApiModelProperty(value = "openid")
	private String openid;

	@ApiModelProperty(value = "类型 dingtalk/wechat/apple")
	private String type;

	@ApiModelProperty(value = "昵称")
	private String nickname;

	@ApiModelProperty(value = "头像")
	private String avatar;

	@ApiModelProperty(value = "附属信息")
	private String ext;

	@ApiModelProperty(value = "对应的用户id")
	private Long userId;

}
