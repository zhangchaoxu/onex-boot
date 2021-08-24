package onexcoder.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class DbConfigRequest implements Serializable {

    private String driverClassName;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String keyword;
    private String tableNames;

    /**
     * doc开头的为 文档相关
     */
    private String docVersion;
    private String docDescription;
    private String docFileName;
    private String docFileType;

    private CodeGenerateConfig codeGenerateConfig;

}
