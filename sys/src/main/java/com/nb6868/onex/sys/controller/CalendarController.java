package com.nb6868.onex.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.dto.CalendarDTO;
import com.nb6868.onex.sys.dto.CalenderDayDateReq;
import com.nb6868.onex.sys.dto.CalenderQueryReq;
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

@RestController("SysCalendar")
@RequestMapping("/sys/calendar/")
@Validated
@Tag(name = "万年历")
public class CalendarController {

    @Autowired
    CalendarService calendarService;

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:calendar", "sys:calendar:query"}, logical = Logical.OR)
    public Result<List<CalendarDTO>> list(@Validated @RequestBody CalenderQueryReq req) {
        QueryWrapper<CalendarEntity> queryWrapper = QueryWrapperHelper.getPredicate(req, "list");
        List<CalendarDTO> list = calendarService.listDto(queryWrapper);

        return new Result<List<CalendarDTO>>().success(list);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:calendar", "sys:calendar:query"}, logical = Logical.OR)
    public Result<PageData<CalendarDTO>> page(@Validated(PageGroup.class) @RequestBody CalenderQueryReq req) {
        QueryWrapper<CalendarEntity> queryWrapper = QueryWrapperHelper.getPredicate(req, "page");
        PageData<CalendarDTO> page = calendarService.pageDto(req, queryWrapper);

        return new Result<PageData<CalendarDTO>>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:calendar", "sys:calendar:query"}, logical = Logical.OR)
    public Result<CalendarDTO> info(@Validated @RequestBody CalenderDayDateReq req) {
        CalendarDTO data = calendarService.oneDto(QueryWrapperHelper.getPredicate(req));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<CalendarDTO>().success(data);
    }

}
