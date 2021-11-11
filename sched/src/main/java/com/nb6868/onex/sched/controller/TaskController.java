package com.nb6868.onex.sched.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.sched.dto.TaskDTO;
import com.nb6868.onex.sched.dto.TaskLogDTO;
import com.nb6868.onex.sched.service.TaskLogService;
import com.nb6868.onex.sched.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
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
    @ApiOperation("分页")
    // @RequiresPermissions("sched:task:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<TaskDTO> page = taskService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    // @RequiresPermissions("sched:task:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        TaskDTO data = taskService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    // @RequiresPermissions("sched:task:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody TaskDTO dto) {
        taskService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    // @RequiresPermissions("sched:task:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody TaskDTO dto) {
        taskService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    // @RequiresPermissions("sched:task:delete")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        taskService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @PutMapping("/run")
    @ApiOperation("立即执行")
    @LogOperation("立即执行")
    // @RequiresPermissions("sched:task:run")
    public Result<?> run(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
		taskService.run(ids);

        return new Result<>();
    }

    @PutMapping("/pause")
    @ApiOperation("暂停")
    @LogOperation("暂停")
    // @RequiresPermissions("sched:task:pause")
    public Result<?> pause(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
		taskService.pause(ids);

        return new Result<>();
    }

    @PutMapping("/resume")
    @ApiOperation("恢复")
    @LogOperation("恢复")
    // @RequiresPermissions("sched:task:resume")
    public Result<?> resume(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
		taskService.resume(ids);

        return new Result<>();
    }

    @GetMapping("logPage")
    @ApiOperation("日志分页")
    // @RequiresPermissions("sched:taskLog:page")
    public Result<?> logPage(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<TaskLogDTO> page = taskLogService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("logInfo")
    @ApiOperation("日志信息")
    // @RequiresPermissions("sched:task:info")
    public Result<?> logInfo(@NotNull(message = "{id.require}") @RequestParam Long id) {
        TaskLogDTO data = taskLogService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

}
