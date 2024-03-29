package com.nb6868.onex.cms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 广告位
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cms_axd")
public class AxdEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 租户编码
	 */
	@TableField(fill = FieldFill.INSERT)
	private String tenantCode;
    /**
     * 标题
     */
	private String name;
    /**
     * 位置
     */
	private String position;
    /**
     * 链接
     */
	private String link;
    /**
     * 备注
     */
	private String remark;
    /**
     * 图片
     */
	private String imgs;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 是否需要登录 0 不需要 1需要
     */
	private Integer needLogin;
}
