package com.nb6868.onex.sched.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.sched.dto.*;
import com.nb6868.onex.sched.service.TaskLogService;
import com.nb6868.onex.sched.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sched/task")
@Validated
@Api(tags = "定时任务")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogService taskLogService;

    @PostMapping("page")
    @ApiOperation("分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sched:task:query")
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody TaskQueryForm form) {
        PageData<?> page = taskService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @ApiOperation("详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sched:task:query")
    @ApiOperationSupport(order = 20)
    public Result<?> info(@Validated @RequestBody IdTenantForm form) {
        TaskDTO data = taskService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sched:task:edit")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 30)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody TaskDTO dto) {
        taskService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @ApiOperationSupport(order = 40)
    @RequiresPermissions("sched:task:edit")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody TaskDTO dto) {
        taskService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @ApiOperationSupport(order = 50)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sched:task:delete")
    public Result<?> delete(@Validated @RequestBody IdTenantForm form) {
        TaskDTO data = taskService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        taskService.delete(form.getId());

        return new Result<>();
    }

    @PostMapping("/runWithParams")
    @ApiOperation("指定参数立即执行")
    @LogOperation("指定参数立即执行")
    @ApiOperationSupport(order = 60)
    @RequiresPermissions("sched:task:edit")
    public Result<?> runWithParams(@Validated @RequestBody TaskRunWithParamsForm form) {
        taskService.runWithParams(form);

        return new Result<>();
    }

    @PostMapping("/pause")
    @ApiOperation("暂停")
    @LogOperation("暂停")
    @RequiresPermissions("sched:task:edit")
    @ApiOperationSupport(order = 70)
    public Result<?> pause(@Validated @RequestBody IdsForm form) {
        taskService.pause(form.getIds());

        return new Result<>();
    }

    @PostMapping("/resume")
    @ApiOperation("恢复")
    @LogOperation("恢复")
    @RequiresPermissions("sched:task:edit")
    @ApiOperationSupport(order = 80)
    public Result<?> resume(@Validated @RequestBody IdsForm form) {
        taskService.resume(form.getIds());

        return new Result<>();
    }

    @PostMapping("logPage")
    @ApiOperation("日志分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sched:taskLog:query")
    @ApiOperationSupport(order = 100)
    public Result<?> logPage(@Validated({PageGroup.class}) @RequestBody TaskLogQueryForm form) {
        PageData<?> page = taskLogService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("logInfo")
    @ApiOperation("日志详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sched:taskLog:query")
    @ApiOperationSupport(order = 110)
    public Result<?> logInfo(@Validated @RequestBody IdTenantForm form) {
        TaskLogDTO data = taskLogService.getDtoById(form.getId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("logDeleteBatch")
    @ApiOperation("日志批量删除")
    @LogOperation("日志批量删除")
    @RequiresPermissions("sched:taskLog:delete")
    @ApiOperationSupport(order = 120)
    public Result<?> logDeleteBatch(@Validated @RequestBody IdsForm form) {
        taskLogService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

}
