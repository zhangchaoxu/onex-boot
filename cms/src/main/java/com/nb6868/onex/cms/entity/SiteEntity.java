package com.nb6868.onex.cms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站点
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cms_site")
public class SiteEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String descr;
    /**
     * 网址
     */
    private String domain;
    /**
     * LOGO
     */
    private String logo;
    /**
     * 版权信息
     */
    private String copyright;
    /**
     * 关键词
     */
    private String keywords;
    /**
     * 生成目录
     */
    private String dir;
    /**
     * 图片
     */
    private String imgs;
    /**
     * 状态
     */
    private Integer state;

}
