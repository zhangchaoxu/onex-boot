package com.nb6868.onexboot.api.modules.log.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("log_release")
public class ReleaseEntity extends BaseEntity {
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
     * 版本号
     */
    private Integer versionNo;
    /**
     * 版本名称
     */
    private String versionName;
    /**
     * 更新记录
     */
    private String content;
    /**
     * 下载链接
     */
    private String downloadLink;
    /**
     * 强制更新
     */
    private Integer forceUpdate;
    /**
     * 显示在下载页面
     */
    private Integer showInPage;
}
