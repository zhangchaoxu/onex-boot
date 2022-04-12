package com.nb6868.onex.msg.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.msg.dto.*;
import com.nb6868.onex.msg.entity.MailLogEntity;
import com.nb6868.onex.msg.entity.MailTplEntity;
import com.nb6868.onex.msg.service.MailLogService;
import com.nb6868.onex.msg.service.MailTplService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@RequestMapping("/msg/mailLog")
@Validated
@Api(tags = "消息记录", position = 20)
public class MailLogController {

    @Autowired
    MailLogService mailLogService;
    @Autowired
    MailTplService mailTplService;

    @PostMapping("page")
    @ApiOperation("分页列表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailLog:query")
    @ApiOperationSupport(order = 20)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody MailLogQueryForm form) {
        QueryWrapper<MailLogEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        PageData<MailLogDTO> page = mailLogService.pageDto(form.getPage(), queryWrapper);

        return new Result<>().success(page);
    }

    @PostMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailLog:delete")
    @ApiOperationSupport(order = 50)
    public Result<?> deleteBatch(@Validated @RequestBody IdsTenantForm form) {
        mailLogService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

    @PostMapping("/send")
    @ApiOperation("发送消息")
    @LogOperation("发送消息")
    @RequiresPermissions("msg:mailLog:send")
    @ApiOperationSupport(order = 60)
    public Result<?> send(@Validated(value = {DefaultGroup.class}) @RequestBody MailSendForm form) {
        MailTplEntity mailTpl = mailTplService.getByCode(form.getTenantCode(), form.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "模板不存在");
        // 发送
        boolean flag = mailLogService.send(mailTpl, form);
        return new Result<>().boolResult(flag);
    }

}
