package com.nb6868.onex.sched.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.nb6868.onex.sched.dto.TaskDTO;
import com.nb6868.onex.sched.dto.TaskLogDTO;
import com.nb6868.onex.sched.dto.TaskQueryForm;
import com.nb6868.onex.sched.dto.TaskRunWithParamsForm;
import com.nb6868.onex.sched.entity.TaskEntity;
import com.nb6868.onex.sched.entity.TaskLogEntity;
import com.nb6868.onex.sched.service.TaskLogService;
import com.nb6868.onex.sched.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
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

    @PostMapping("page")
    @ApiOperation("任务分页列表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sched:task:query")
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody TaskQueryForm form) {
        QueryWrapper<TaskEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        PageData<TaskDTO> page = taskService.pageDto(form.getPage(), queryWrapper);

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @ApiOperation("任务详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sched:task:query")
    @ApiOperationSupport(order = 20)
    public Result<?> info(@Validated @RequestBody IdTenantForm form) {
        TaskDTO data = taskService.getDtoById(form.getId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("任务保存")
    @LogOperation("任务保存")
    @RequiresPermissions("sched:task:edit")
    @ApiOperationSupport(order = 30)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody TaskDTO dto) {
        taskService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("任务修改")
    @LogOperation("任务修改")
    @ApiOperationSupport(order = 40)
    @RequiresPermissions("sched:task:edit")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody TaskDTO dto) {
        taskService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("deleteBatch")
    @ApiOperation("任务批量删除")
    @LogOperation("任务批量删除")
    @ApiOperationSupport(order = 50)
    @RequiresPermissions("sched:task:edit")
    public Result<?> deleteBatch(@Validated @RequestBody IdsForm form) {
        taskService.logicDeleteByIds(form.getIds());

        return new Result<>();
    }

    @PostMapping("/runWithParams")
    @ApiOperation("任务指定参数立即执行")
    @LogOperation("任务指定参数立即执行")
    @ApiOperationSupport(order = 60)
    @RequiresPermissions("sched:task:edit")
    public Result<?> runWithParams(@Validated @RequestBody TaskRunWithParamsForm form) {
        taskService.runWithParams(form);

        return new Result<>();
    }

    @PostMapping("/pause")
    @ApiOperation("任务暂停")
    @LogOperation("任务暂停")
    @RequiresPermissions("sched:task:edit")
    @ApiOperationSupport(order = 70)
    public Result<?> pause(@Validated @RequestBody IdsForm form) {
        taskService.pause(form.getIds());

        return new Result<>();
    }

    @PostMapping("/resume")
    @ApiOperation("任务恢复")
    @LogOperation("任务恢复")
    @RequiresPermissions("sched:task:edit")
    @ApiOperationSupport(order = 80)
    public Result<?> resume(@Validated @RequestBody IdsForm form) {
        taskService.resume(form.getIds());

        return new Result<>();
    }

    @PostMapping("logPage")
    @ApiOperation("任务日志分页列表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sched:task:query")
    @ApiOperationSupport(order = 100)
    public Result<?> logPage(@Validated({PageGroup.class}) @RequestBody TaskQueryForm form) {
        QueryWrapper<TaskLogEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        PageData<TaskLogDTO> page = taskLogService.pageDto(form.getPage(), queryWrapper);

        return new Result<>().success(page);
    }

    @GetMapping("logInfo")
    @ApiOperation("任务日志详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sched:task:query")
    @ApiOperationSupport(order = 110)
    public Result<?> logInfo(@Validated @RequestBody IdTenantForm form) {
        TaskLogDTO data = taskLogService.getDtoById(form.getId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

}
