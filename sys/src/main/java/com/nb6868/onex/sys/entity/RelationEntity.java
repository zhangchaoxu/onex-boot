package com.nb6868.onex.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 系统-关系表
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_relation")
@Alias("sys_relation")
public class RelationEntity extends BaseEntity {
    /**
     * 类型
     */
    private String type;
    /**
     * 左表ID
     */
    private Long leftId;
    /**
     * 右表ID
     */
    private Long rightId;
    /**
     * 排序
     */
    private Integer sort;
}
