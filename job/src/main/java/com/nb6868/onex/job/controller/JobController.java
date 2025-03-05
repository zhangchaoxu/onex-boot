package com.nb6868.onex.job.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.IdsReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.job.dto.*;
import com.nb6868.onex.job.entity.JobEntity;
import com.nb6868.onex.job.service.JobLogService;
import com.nb6868.onex.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("SysJob")
@RequestMapping("/sys/job/")
@Validated
@Tag(name = "定时任务")
public class JobController {

    @Autowired
    JobService jobService;
    @Autowired
    JobLogService jobLogService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:query"}, logical = Logical.OR)
    public Result<PageData<JobDTO>> page(@Validated({PageGroup.class}) @RequestBody JobQueryReq form) {
        PageData<JobDTO> page = jobService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<PageData<JobDTO>>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:query"}, logical = Logical.OR)
    public Result<JobDTO> info(@Validated @RequestBody IdReq form) {
        JobDTO data = jobService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<JobDTO>().success(data);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增或更新")
    @LogOperation("新增或更新")
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:edit"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<JobDTO> saveOrUpdate(@Validated @RequestBody JobSaveOrUpdateReq req) {
        JobEntity entity = jobService.saveOrUpdateByReq(req);
        JobDTO dto = ConvertUtils.sourceToTarget(entity, JobDTO.class);
        return new Result<JobDTO>().success(dto);
    }


    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:delete"}, logical = Logical.OR)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(jobService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        jobService.removeById(req.getId());
        return new Result<>();
    }

    @PostMapping("/runWithParams")
    @Operation(summary = "指定参数立即执行")
    @LogOperation("指定参数立即执行")
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:run"}, logical = Logical.OR)
    public Result<?> runWithParams(@Validated @RequestBody JobRunWithParamsReq form) {
        jobService.runWithParams(form);

        return new Result<>();
    }

    @PostMapping("logPage")
    @Operation(summary = "日志分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:jobLog:query"}, logical = Logical.OR)
    public Result<PageData<JobLogDTO>> logPage(@Validated({PageGroup.class}) @RequestBody JobLogQueryReq form) {
        PageData<JobLogDTO> page = jobLogService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<PageData<JobLogDTO>>().success(page);
    }

    @PostMapping("logInfo")
    @Operation(summary = "日志详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:jobLog:query"}, logical = Logical.OR)
    public Result<JobLogDTO> logInfo(@Validated @RequestBody IdReq form) {
        JobLogDTO data = jobLogService.getDtoById(form.getId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<JobLogDTO>().success(data);
    }

    @PostMapping("logDeleteBatch")
    @Operation(summary = "日志批量删除")
    @LogOperation("日志批量删除")
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:jobLog:delete"}, logical = Logical.OR)
    public Result<?> logDeleteBatch(@Validated @RequestBody IdsReq req) {
        jobLogService.removeByIds(req.getIds());

        return new Result<>();
    }

    @PostMapping("itemPage")
    @Operation(summary = "明细分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:jobLog:query"}, logical = Logical.OR)
    public Result<PageData<JobLogDTO>> itemPage(@Validated({PageGroup.class}) @RequestBody JobLogQueryReq form) {
        PageData<JobLogDTO> page = jobLogService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<PageData<JobLogDTO>>().success(page);
    }

    @PostMapping("logInfo")
    @Operation(summary = "日志详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:jobLog:query"}, logical = Logical.OR)
    public Result<JobLogDTO> logInfo(@Validated @RequestBody IdReq form) {
        JobLogDTO data = jobLogService.getDtoById(form.getId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<JobLogDTO>().success(data);
    }

    @PostMapping("logDeleteBatch")
    @Operation(summary = "日志批量删除")
    @LogOperation("日志批量删除")
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:jobLog:delete"}, logical = Logical.OR)
    public Result<?> logDeleteBatch(@Validated @RequestBody IdsReq req) {
        jobLogService.removeByIds(req.getIds());

        return new Result<>();
    }

}
