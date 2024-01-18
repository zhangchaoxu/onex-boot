package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 角色
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_role")
@Alias("uc_role")
public class RoleEntity extends BaseEntity {

    /**
     * 名称
     */
	private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 备注
     */
	private String remark;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 租户编码
     */
	private String tenantCode;

}
