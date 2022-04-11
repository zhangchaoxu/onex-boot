package com.nb6868.onex.sys.entity;

import com.nb6868.onex.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict")
public class DictEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 上级ID，一级为0
     */
    private Long pid;
    /**
     * 字典类型
     */
    private String type;
    /**
     * 字典名称
     */
    private String name;
    /**
     * 字典值
     */
    private String value;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer sort;

}
