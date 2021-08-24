package com.nb6868.onex.coder.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 代码生成配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class CodeGenerateConfig implements Serializable {

    /**
     * 表前缀
     */
    private String tablePrefix;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 模块名
     */
    private String moduleName;
    /**
     * 作者名
     */
    private String authorName;
    /**
     * 作者邮箱
     */
    private String authorEmail;
    /**
     * 版本号
     */
    private String version;

}
