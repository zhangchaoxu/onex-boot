package com.nb6868.onex.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.dto.CalendarDTO;
import com.nb6868.onex.sys.dto.CalenderDayDateForm;
import com.nb6868.onex.sys.dto.CalenderQueryForm;
import com.nb6868.onex.sys.entity.CalendarEntity;
import com.nb6868.onex.sys.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/calendar")
@Validated
@Tag(name = "万年历")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:calendar", "sys:calendar:query"}, logical = Logical.OR)
    public Result<?> list(@Validated @RequestBody CalenderQueryForm form) {
        QueryWrapper<CalendarEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "list");
        List<CalendarDTO> list = calendarService.listDto(queryWrapper);

        return new Result<>().success(list);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:calendar", "sys:calendar:query"}, logical = Logical.OR)
    public Result<?> page(@Validated(PageGroup.class) @RequestBody CalenderQueryForm form) {
        QueryWrapper<CalendarEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");
        PageData<CalendarDTO> page = calendarService.pageDto(form, queryWrapper);

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:calendar", "sys:calendar:query"}, logical = Logical.OR)
    public Result<?> info(@Validated @RequestBody CalenderDayDateForm form) {
        CalendarDTO data = calendarService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

}
