package com.nb6868.onex.modules.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_user_oauth")
public class UserOauthEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;

    /**
     * appid
     */
	private String appid;
    /**
     * unionid
     */
	private String unionid;
    /**
     * openid
     */
	private String openid;
    /**
     * 类型 dingtalk/wechat/apple
	 *
     */
	private String type;
    /**
     * 昵称
     */
	private String nickname;
    /**
     * 头像
     */
	private String avatar;
    /**
     * 附属信息
     */
	private String ext;
    /**
     * 对应的用户id
     */
	private Long userId;
}
