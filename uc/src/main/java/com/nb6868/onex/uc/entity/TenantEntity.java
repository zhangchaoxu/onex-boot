package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_tenant")
@Alias("uc_tenant")
public class TenantEntity extends BaseEntity {
    /**
     * 编码,需唯一
     */
	private String code;
    /**
     * 名称
     */
	private String name;
    /**
     * 备注
     */
	private String remark;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 状态
     */
	private Integer state;
}
