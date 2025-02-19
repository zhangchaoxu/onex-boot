package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "文件上传结果")
public class FileUploadRes extends BaseRes {

    @Schema(description = "文件uuid")
    private String uuid;

    @Schema(description = "访问链接")
    private String url;

    @Schema(description = "文件名")
    private String filename;

}
