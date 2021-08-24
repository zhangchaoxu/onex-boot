package com.nb6868.onex.api.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_region")
public class RegionEntity implements Serializable {

    /**
     * 区域编号
     */
    @TableId(type = IdType.INPUT)
    private Long id;
    /**
     * 上级区域编号,0为一级
     */
    private Long pid;
    /**
     * 名称
     */
    private String name;
    /**
     * 拼音
     */
    private String pinyin;
    /**
     * 拼音前缀
     */
    private String pinyinPrefix;
    /**
     * 区号
     */
    private String code;
    /**
     * 邮编
     */
    private String postcode;
    /**
     * 原始名称
     */
    private String extName;
    /**
     * 原始编号
     */
    private String extId;
    /**
     * 层级深度
     */
    private Integer deep;
    /**
     * 中心点,GCJ-02.格式："lng lat" or "EMPTY"
     */
    private String geo;
    /**
     * 边界坐标点,GCJ-02
     */
    private String polygon;

}
