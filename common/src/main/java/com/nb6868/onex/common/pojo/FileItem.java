package com.nb6868.onex.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文件，只需要链接和名称
 * 可在使用时
 * @Schema(description = "图片数组")
 *
 * @Valid
 * @NotNull(message = "图片数组需传入内容", groups = {DefaultGroup.class})
 * private List<FileItem> imgs;
 * 记得加上校验@Validated(value = {jakarta.validation.groups.Default.class})
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "文件")
public class FileItem implements Serializable {

    @Schema(description = "文件名")
    @NotEmpty(message = "文件名不能为空")
    private String name;

    @Schema(description = "文件链接")
    @Pattern(regexp = "^https?://.*$", message = "文件链接无效,请确认文件上传已完成")
    private String url;

}
