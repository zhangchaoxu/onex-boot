package com.nb6868.onexboot.api.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 短地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_shorturl")
public class ShorturlEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
	private String name;
    /**
     * 完整场地址
     */
	private String url;
    /**
     * 短地址路径
     */
	private String code;
    /**
     * 备注
     */
	private String remark;
    /**
     * 状态 0 不开放 1 开放
     */
	private Integer status;
}
