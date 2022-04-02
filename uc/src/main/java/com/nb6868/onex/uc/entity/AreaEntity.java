package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_area")
@Alias("uc_area")
public class AreaEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
	private String code;
    /**
     * 上级编码
     */
	private String pcode;
	/**
	 * 别名
	 */
	private String alias;
    /**
     * 名称
     */
	private String name;
    /**
     * 租户编码
     */
	private String tenantCode;
}
