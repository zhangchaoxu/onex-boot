package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_user")
@Alias("uc_user")
public class UserEntity extends BaseEntity {

    /**
     * 部门ID
     */
	private Long deptId;
    /**
     * 编号
     */
	private String code;
    /**
     * 用户名
     */
	private String username;
    /**
     * 密码
     */
	private String password;
    /**
     * 密码RAW
     */
	private String passwordRaw;
    /**
     * 真实姓名
     */
	private String realName;
    /**
     * 邀请码
     */
	private String inviteCode;
    /**
     * 昵称
     */
	private String nickname;
    /**
     * 手机号
     */
	private String mobile;
    /**
     * 邮箱
     */
	private String email;
    /**
     * 身份证号
     */
	private String idNo;
    /**
     * 生日
     */
	private Date birthday;
    /**
     * 头像
     */
	private String avatar;
    /**
     * 备注
     */
	private String remark;
    /**
     * 性别   0：男   1：女    2：保密
     */
	private Integer gender;
    /**
     * 账户余额
     */
	private BigDecimal balance;
    /**
     * 积分
     */
	private BigDecimal points;
    /**
     * 收入余额
     */
	private BigDecimal income;
    /**
     * 类型
     */
	private Integer type;
    /**
     * 状态  0：停用   1：正常  2：锁定
     */
	private Integer state;
    /**
     * 租户编码
     */
	private String tenantCode;
    /**
     * 角色编码
     */
    @TableField(exist = false)
    private String roleCodes;
    /**
     * 角色名称
     */
    @TableField(exist = false)
    private String roleNames;
}
