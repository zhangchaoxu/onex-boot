package com.nb6868.onex.uc.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.nb6868.onex.common.jpa.Jackson2TypeHandler;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 部门
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_dept")
@Alias("uc_dept")
public class DeptEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 上级ID
	 */
	private Long pid;
	/**
	 * 类型
	 */
	private Integer type;
    /**
     * 编码
     */
	private String code;
    /**
     * 上级编码
     */
	private String pcode;
	/**
	 * 区域编码
	 */
	private String areaCode;
	/**
	 * 第三方部门信息
	 */
	@TableField(typeHandler = Jackson2TypeHandler.class)
	private JSONObject oauthInfo;
	/**
	 * 第三方部门id
	 */
	private String oauthDeptid;
    /**
     * 名称
     */
	private String name;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 租户编码
     */
	private String tenantCode;
}
