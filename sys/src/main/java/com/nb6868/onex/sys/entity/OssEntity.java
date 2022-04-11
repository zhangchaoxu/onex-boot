package com.nb6868.onex.sys.entity;

import com.nb6868.onex.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * URL地址
     */
    private String url;

    /**
     * 文件尺寸
     */
    private Long size;

    /**
     * 类型
     */
    private String contentType;

    /**
     * 文件名
     */
    private String filename;

}
