package com.nb6868.onex.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 素材库
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_oss")
public class OssEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    // @TableField(fill = FieldFill.INSERT)
    private String uuid;
    /**
     * URL地址
     */
    private String url;
    /**
     * 文件尺寸
     */
    private Long size;
    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 文件名
     */
    private String filename;
    /**
     * 存储路径
     */
    private String path;
    /**
     * 类型
     */
    private String type;

}
