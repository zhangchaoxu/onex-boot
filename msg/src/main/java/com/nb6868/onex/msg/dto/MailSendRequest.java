package com.nb6868.onex.msg.dto;

import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.msg.MsgConst;
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
 * 消息发送请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息发送请求")
public class MailSendRequest implements Serializable {

    @ApiModelProperty(value = "模板编码", required = true)
    @NotBlank(message = "模板编码不能为空", groups = AddGroup.class)
    private String tplCode = MsgConst.SMS_TPL_LOGIN;

    @ApiModelProperty(value = "收件人", required = true)
    @NotBlank(message = "收件人不能为空", groups = AddGroup.class)
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
