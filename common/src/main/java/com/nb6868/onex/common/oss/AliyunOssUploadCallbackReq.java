package com.nb6868.onex.common.oss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "阿里云上传回调请求", description = "具体定义见https://www.alibabacloud.com/help/zh/oss/developer-reference/callback")
public class AliyunOssUploadCallbackReq implements Serializable {

    @Schema(description = "存储空间名称")
    private String bucket;

    @Schema(description = "对象（文件）的完整路径,不包含endpoint前缀")
    private String object;

    @Schema(description = "文件的ETag")
    private String etag;

    @Schema(description = "Object大小")
    private String size;

    @Schema(description = "资源类型")
    private String mimeType;

    @Schema(description = "与上传文件后返回的x-oss-hash-crc64ecma头内容一致")
    private String crc64;

    @Schema(description = "与上传文件后返回的Content-MD5头内容一致")
    private String contentMd5;

    @Schema(description = "客户端所在的VpcId")
    private String vpcId;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "请求的RequestId")
    private String reqId;

    @Schema(description = "发起请求的接口名称，例如PutObject、PostObject")
    private String operation;

}
