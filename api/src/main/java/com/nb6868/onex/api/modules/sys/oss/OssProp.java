package com.nb6868.onex.api.modules.sys.oss;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 云存储配置信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@ApiModel(value = "云存储配置信息")
public class OssProp implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型 aliyun：阿里云  local：本地上传")
    @NotBlank(message = "类型不能为空")
    private String type;

    @ApiModelProperty(value = "绑定的域名")
    @NotBlank(message = "域名不能为空")
    @URL(message = "{aliyun.domain.url}")
    private String domain;

    @ApiModelProperty("保留文件名")
    private Boolean keepFileName;

    @ApiModelProperty("安全访问")
    private Boolean secure;

    @ApiModelProperty(value = "角色ARN")
    private String roleArn;

    @ApiModelProperty(value = "区域")
    private String region;

    @ApiModelProperty(value = "角色SessionName")
    private String roleSessionName;

    @ApiModelProperty(value = "STS有效秒数")
    private Long stsDurationSeconds;

    @ApiModelProperty(value = "路径前缀")
    private String prefix;

    @ApiModelProperty(value = "EndPoint")
    @NotBlank(message = "{aliyun.endPoint.require}")
    private String endPoint;

    @ApiModelProperty(value = "AccessKeyId")
    @NotBlank(message = "{aliyun.accesskeyid.require}")
    private String accessKeyId;

    @ApiModelProperty(value = "AccessKeySecret")
    @NotBlank(message = "{aliyun.accesskeysecret.require}")
    private String accessKeySecret;

    @ApiModelProperty(value = "阿里云BucketName")
    @NotBlank(message = "{aliyun.bucketname.require}")
    private String bucketName;

    @ApiModelProperty(value = "本地上传存储目录")
    @NotBlank(message = "{local.path.url}")
    private String localPath;

}
