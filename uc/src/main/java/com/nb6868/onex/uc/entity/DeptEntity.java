package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
     * 所有上级ID，用逗号分开
     */
	private String pids;
    /**
     * 部门名称
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
