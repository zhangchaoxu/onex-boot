package com.nb6868.onex.modules.sys.entity;

import com.nb6868.onex.booster.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_region")
public class RegionEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 上级区域编码,0为跟目录
     */
    private Long pid;
    /**
     * 区域名称
     */
    private String name;
    /**
     * 区域邮编
     */
    private String code;
    /**
     * 区域级别
     */
    private Integer level;
    /**
     * 区域级别名称
     */
    private String levelName;
    /**
     * 中心点
     */
    private String center;
    /**
     * 边界坐标点
     */
    private String polyline;

    /**
     * 下级节点数量
     */
    @TableField(exist = false)
    private Integer childNum;
}
