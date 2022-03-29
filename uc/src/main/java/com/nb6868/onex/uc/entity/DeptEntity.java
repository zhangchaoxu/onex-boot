package com.nb6868.onex.uc.entity;

import com.nb6868.onex.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_dept")
public class DeptEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 上级ID
     */
    private Long pid;
    /**
     * 名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 上级部门名称
     */
    @TableField(exist = false)
    private String parentName;

}
