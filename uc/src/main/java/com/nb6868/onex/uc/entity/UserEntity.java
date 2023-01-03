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
     * 类型
     */
    private Integer type;
    /**
     * 编号
     */
    private String code;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 等级
     */
    private String level;
    /**
     * 部门编码
     */
	private String deptCode;
    /**
     * 区域编码
     */
    private String areaCode;
    /**
     * 用户名
     */
	private String username;
    /**
     * 岗位编码
     */
    private String postCode;
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
     * 头像
     */
	private String avatar;
    /**
     * 备注
     */
	private String remark;
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
     * 租户编码
     */
	private String tenantCode;
}
