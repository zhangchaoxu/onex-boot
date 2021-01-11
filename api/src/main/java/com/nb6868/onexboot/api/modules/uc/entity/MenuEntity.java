package com.nb6868.onexboot.api.modules.uc.entity;

import com.nb6868.onexboot.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_menu")
public class MenuEntity extends BaseEntity {
	/**
	 * 父菜单ID，一级菜单为0
	 */
	private Long pid;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 英文名称
	 */
	private String nameEn;
	/**
	 * 菜单URL
	 */
	private String url;
	/**
	 * 是否显示
	 */
	private Integer showMenu;
	/**
	 * 菜单新页面打开
	 */
	private Integer urlNewBlank;
	/**
	 * 授权
	 */
	private String permissions;
	/**
	 * 类型   0:菜单/页面   1:按钮/接口
	 */
	private Integer type;
	/**
	 * 菜单图标
	 */
	private String icon;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 上级菜单名称
	 */
	@TableField(exist = false)
	private String parentName;

}
