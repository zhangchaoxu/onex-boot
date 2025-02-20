package com.nb6868.onex.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文件，带有uuid
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "Uuid文件")
public class FileUuidItem extends UuidReq {

    @Schema(description = "文件名")
    private String name;

    @Schema(description = "文件链接")
    private String url;

    @Schema(description = "文件大小")
    private int size;

}
