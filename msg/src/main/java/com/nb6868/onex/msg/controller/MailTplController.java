package com.nb6868.onex.msg.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdTenantForm;
import com.nb6868.onex.common.pojo.IdsTenantForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.msg.dto.*;
import com.nb6868.onex.msg.entity.MailTplEntity;
import com.nb6868.onex.msg.service.MailLogService;
import com.nb6868.onex.msg.service.MailTplService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消息模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/msg/mailTpl")
@Validated
@Api(tags = "消息模板", position = 10)
@ApiSupport(order = 10)
public class MailTplController {

    @Autowired
    MailTplService mailTplService;
    @Autowired
    MailLogService mailLogService;

    @PostMapping("page")
    @ApiOperation("分页列表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailTpl:query")
    @ApiOperationSupport(order = 20)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody MailTplQueryForm form) {
        PageData<?> page = mailTplService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("list")
    @ApiOperation("列表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailTpl:query")
    @ApiOperationSupport(order = 10)
    public Result<?> list(@Validated @RequestBody MailTplQueryForm form) {
        List<?> list = mailTplService.listDto(QueryWrapperHelper.getPredicate(form));
        return new Result<>().success(list);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailTpl:query")
    @ApiOperationSupport(order = 30)
    public Result<?> info(@Validated @RequestBody IdTenantForm form) {
        MailTplDTO data = mailTplService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("msg:mailTpl:edit")
    @ApiOperationSupport(order = 40)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody MailTplDTO dto) {
        mailTplService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("msg:mailTpl:edit")
    @ApiOperationSupport(order = 50)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody MailTplDTO dto) {
        mailTplService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailTpl:delete")
    @ApiOperationSupport(order = 60)
    public Result<?> delete(@Validated @RequestBody IdTenantForm form) {
        mailTplService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

    @PostMapping("logPage")
    @ApiOperation("日志分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailLog:query")
    @ApiOperationSupport(order = 100)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody MailLogQueryForm form) {
        PageData<?> page = mailLogService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("/send")
    @ApiOperation("发送消息")
    @LogOperation("发送消息")
    @RequiresPermissions("msg:mailLog:send")
    @ApiOperationSupport(order = 110)
    public Result<?> send(@Validated(value = {DefaultGroup.class}) @RequestBody MailSendForm form) {
        MailTplEntity mailTpl = mailTplService.getByCode(form.getTenantCode(), form.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "模板不存在");
        // 发送
        boolean flag = mailLogService.send(mailTpl, form);
        return new Result<>().boolResult(flag);
    }

    @PostMapping("logDeleteBatch")
    @ApiOperation("记录批量删除")
    @LogOperation("记录批量删除")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailLog:delete")
    @ApiOperationSupport(order = 50)
    public Result<?> logDeleteBatch(@Validated @RequestBody IdsTenantForm form) {
        mailLogService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

}
