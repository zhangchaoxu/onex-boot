package com.nb6868.onexboot.api.modules.msg.controller;

import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.msg.MsgConst;
import com.nb6868.onexboot.api.modules.msg.dto.MailLogDTO;
import com.nb6868.onexboot.api.modules.msg.dto.MailSendRequest;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.api.modules.msg.service.MailTplService;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 消息记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/msg/mailLog")
@Validated
@Api(tags = "消息记录")
public class MailLogController {

    @Autowired
    MailLogService mailLogService;
    @Autowired
    MailTplService mailTplService;

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("msg:mailLog:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<MailLogDTO> page = mailLogService.pageDto(params);

        return new Result<>().success(page);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("msg:mailLog:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        mailLogService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("msg:mailLog:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        mailLogService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @PostMapping("/send")
    @ApiOperation("发送消息")
    @LogOperation("发送消息")
    @RequiresPermissions("msg:mailLog:save")
    public Result<?> send(@Validated(value = {AddGroup.class}) @RequestBody MailSendRequest dto) {
        boolean flag = mailLogService.send(dto);
        if (flag) {
            return new Result<>();
        }

        return new Result<>().error("消息发送失败");
    }

    @PostMapping("sendCode")
    @ApiOperation("发送验证码消息")
    @LogOperation("发送验证码消息")
    public Result<?> sendCode(@Validated(value = {AddGroup.class}) @RequestBody MailSendRequest dto) {
        // 只允许发送CODE_开头的模板
        AssertUtils.isFalse(dto.getTplCode().startsWith(MsgConst.SMS_CODE_TPL_PREFIX), "只支持" + MsgConst.SMS_CODE_TPL_PREFIX + "类型模板发送");
        boolean flag = mailLogService.send(dto);
        if (flag) {
            return new Result<>();
        }
        return new Result<>().error("消息发送失败");
    }

}
