package com.nb6868.onexboot.api.modules.msg.dto;

import com.nb6868.onexboot.common.validator.group.AddGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * 邮件发送请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "邮件发送请求")
public class MailSendRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模板编码", required = true)
    @NotBlank(message = "模板编码不能为空", groups = AddGroup.class)
    private String tplCode;

    @ApiModelProperty(value = "收件人", required = true)
    @NotEmpty(message = "收件人不能为空", groups = AddGroup.class)
    private String mailTo;

    @ApiModelProperty(value = "抄送人")
    private String mailCc;

    @ApiModelProperty(value = "标题参数")
    private String titleParam;

    @ApiModelProperty(value = "内容参数")
    private String contentParam;

    @ApiModelProperty(value = "附件")
    List<File> attachments;

}
