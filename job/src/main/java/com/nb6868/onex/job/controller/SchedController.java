package com.nb6868.onex.job.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdTenantForm;
import com.nb6868.onex.common.pojo.IdsForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.job.dto.JobDTO;
import com.nb6868.onex.job.dto.JobLogDTO;
import com.nb6868.onex.job.dto.JobLogQueryForm;
import com.nb6868.onex.job.dto.JobQueryForm;
import com.nb6868.onex.job.dto.JobRunWithParamsForm;
import com.nb6868.onex.sys.service.JobLogService;
import com.nb6868.onex.sys.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/job/")
@Validated
@Api(tags = "定时任务", position = 20)
public class SchedController {

    @Autowired
    private JobService jobService;
    @Autowired
    private JobLogService jobLogService;

    @PostMapping("page")
    @ApiOperation("分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sys:sched:query")
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody JobQueryForm form) {
        PageData<?> page = jobService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @ApiOperation("详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sys:sched:query")
    @ApiOperationSupport(order = 20)
    public Result<?> info(@Validated @RequestBody IdTenantForm form) {
        JobDTO data = jobService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:sched:edit")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 30)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody JobDTO dto) {
        jobService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @ApiOperationSupport(order = 40)
    @RequiresPermissions("sys:sched:edit")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody JobDTO dto) {
        jobService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @ApiOperationSupport(order = 50)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sys:sched:delete")
    public Result<?> delete(@Validated @RequestBody IdTenantForm form) {
        JobDTO data = jobService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        jobService.delete(form.getId());

        return new Result<>();
    }

    @PostMapping("/runWithParams")
    @ApiOperation("指定参数立即执行")
    @LogOperation("指定参数立即执行")
    @ApiOperationSupport(order = 60)
    @RequiresPermissions("sys:sched:edit")
    public Result<?> runWithParams(@Validated @RequestBody JobRunWithParamsForm form) {
        jobService.runWithParams(form);

        return new Result<>();
    }

    @PostMapping("/pause")
    @ApiOperation("暂停")
    @LogOperation("暂停")
    @RequiresPermissions("sys:sched:edit")
    @ApiOperationSupport(order = 70)
    public Result<?> pause(@Validated @RequestBody IdsForm form) {
        jobService.pause(form.getIds());

        return new Result<>();
    }

    @PostMapping("/resume")
    @ApiOperation("恢复")
    @LogOperation("恢复")
    @RequiresPermissions("sys:sched:edit")
    @ApiOperationSupport(order = 80)
    public Result<?> resume(@Validated @RequestBody IdsForm form) {
        jobService.resume(form.getIds());

        return new Result<>();
    }

    @PostMapping("logPage")
    @ApiOperation("日志分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sys:schedLog:query")
    @ApiOperationSupport(order = 100)
    public Result<?> logPage(@Validated({PageGroup.class}) @RequestBody JobLogQueryForm form) {
        PageData<?> page = jobLogService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("logInfo")
    @ApiOperation("日志详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sys:schedLog:query")
    @ApiOperationSupport(order = 110)
    public Result<?> logInfo(@Validated @RequestBody IdTenantForm form) {
        JobLogDTO data = jobLogService.getDtoById(form.getId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("logDeleteBatch")
    @ApiOperation("日志批量删除")
    @LogOperation("日志批量删除")
    @RequiresPermissions("sys:schedLog:delete")
    @ApiOperationSupport(order = 120)
    public Result<?> logDeleteBatch(@Validated @RequestBody IdsForm form) {
        jobLogService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

}
