package com.nb6868.onex.sched.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.CommonForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.sched.dto.TaskDTO;
import com.nb6868.onex.sched.dto.TaskLogDTO;
import com.nb6868.onex.sched.dto.TaskRunWithParamsForm;
import com.nb6868.onex.sched.service.TaskLogService;
import com.nb6868.onex.sched.service.TaskService;
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

@RestController
@RequestMapping("/sched/task")
@Validated
@Api(tags = "定时任务")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogService taskLogService;

    @GetMapping("page")
    @ApiOperation("任务分页列表")
    @RequiresPermissions("sched:task:info")
    @ApiOperationSupport(order = 100)
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<TaskDTO> page = taskService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("任务详情")
    @RequiresPermissions("sched:task:info")
    @ApiOperationSupport(order = 120)
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        TaskDTO data = taskService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("任务保存")
    @LogOperation("任务保存")
    @RequiresPermissions("sched:task:update")
    @ApiOperationSupport(order = 130)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody TaskDTO dto) {
        taskService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("任务修改")
    @LogOperation("任务修改")
    @ApiOperationSupport(order = 140)
    @RequiresPermissions("sched:task:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody TaskDTO dto) {
        taskService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("deleteBatch")
    @ApiOperation("任务批量删除")
    @LogOperation("任务批量删除")
    @ApiOperationSupport(order = 150)
    @RequiresPermissions("sched:task:update")
    public Result<?> deleteBatch(@Validated(value = {CommonForm.ListGroup.class}) @RequestBody CommonForm form) {
        taskService.logicDeleteByIds(form.getIds());

        return new Result<>();
    }

    @PostMapping("/runWithParams")
    @ApiOperation("任务指定参数立即执行")
    @LogOperation("任务指定参数立即执行")
    @ApiOperationSupport(order = 160)
    @RequiresPermissions("sched:task:update")
    public Result<?> runWithParams(@Validated(value = {DefaultGroup.class}) @RequestBody TaskRunWithParamsForm requestBody) {
        taskService.runWithParams(requestBody);

        return new Result<>();
    }

    @PostMapping("/pause")
    @ApiOperation("任务暂停")
    @LogOperation("任务暂停")
    @RequiresPermissions("sched:task:update")
    @ApiOperationSupport(order = 170)
    public Result<?> pause(@Validated(value = {CommonForm.ListGroup.class}) @RequestBody CommonForm form) {
		taskService.pause(form.getIds());

        return new Result<>();
    }

    @PostMapping("/resume")
    @ApiOperation("任务恢复")
    @LogOperation("任务恢复")
    @RequiresPermissions("sched:task:update")
    @ApiOperationSupport(order = 180)
    public Result<?> resume(@Validated(value = {CommonForm.ListGroup.class}) @RequestBody CommonForm form) {
		taskService.resume(form.getIds());

        return new Result<>();
    }

    @GetMapping("logPage")
    @ApiOperation("任务日志分页列表")
    @RequiresPermissions("sched:task:info")
    @ApiOperationSupport(order = 200)
    public Result<?> logPage(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<TaskLogDTO> page = taskLogService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("logInfo")
    @ApiOperation("任务日志详情")
    @RequiresPermissions("sched:task:info")
    @ApiOperationSupport(order = 210)
    public Result<?> logInfo(@NotNull(message = "{id.require}") @RequestParam Long id) {
        TaskLogDTO data = taskLogService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

}
