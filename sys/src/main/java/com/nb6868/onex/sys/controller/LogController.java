package com.nb6868.onex.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdsForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.dto.LogDTO;
import com.nb6868.onex.sys.dto.LogQueryForm;
import com.nb6868.onex.sys.entity.LogEntity;
import com.nb6868.onex.sys.service.LogService;
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

@RestController
@RequestMapping("/sys/log/")
@Validated
@Tag(name = "日志管理")
public class LogController {

    @Autowired
    LogService logService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:log", "sys:log:query"}, logical = Logical.OR)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody LogQueryForm form) {
        QueryWrapper<LogEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");
        PageData<LogDTO> page = logService.pageDto(form, queryWrapper);

        return new Result<>().success(page);
    }

    @PostMapping("deleteBatch")
    @Operation(summary = "批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:log", "sys:log:delete"}, logical = Logical.OR)
    public Result<?> deleteBatch(@Validated @RequestBody IdsForm form) {
        logService.removeByIds(form.getIds());

        return new Result<>();
    }

}
