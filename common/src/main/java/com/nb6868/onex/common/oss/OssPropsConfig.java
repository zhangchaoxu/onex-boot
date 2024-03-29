package com.nb6868.onex.common.oss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 存储配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class OssPropsConfig {

    @Schema(description = "类型 aliyun阿里云/huaweicloud华为云/awss3亚马逊S3/local本地")
    private String type;

    @Schema(description = "类名称")
    private String serviceClassName;

    @Schema(description = "绑定的域名")
    private String domain;

    @Schema(description = "保留文件名")
    private Boolean keepFileName;

    @Schema(description = "安全访问")
    private Boolean secure;

    @Schema(description = "角色ARN")
    private String roleArn;

    @Schema(description = "区域")
    private String region;

    @Schema(description = "角色SessionName")
    private String roleSessionName;

    @Schema(description = "STS有效秒数")
    private Long stsDurationSeconds;

    @Schema(description = "全局路径前缀")
    private String prefix;

    @Schema(description = "EndPoint")
    private String endPoint;

    @Schema(description = "AccessKeyId")
    private String accessKeyId;

    @Schema(description = "AccessKeySecret")
    private String accessKeySecret;

    @Schema(description = "s3对应BucketName/本地存储对应本地路径")
    private String bucketName;

    @Schema(description = "是否保存到存储记录")
    private Boolean saveDb;

    @Schema(description = "路径策略")
    private String pathPolicy;

}
