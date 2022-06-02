package com.nb6868.onex.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.dto.CalendarDTO;
import com.nb6868.onex.sys.dto.CalenderDayDateForm;
import com.nb6868.onex.sys.dto.CalenderQueryForm;
import com.nb6868.onex.sys.entity.CalendarEntity;
import com.nb6868.onex.sys.service.CalendarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/calendar")
@Validated
@Api(tags = "万年历", position = 110)
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @PostMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("sys:calendar:query")
    public Result<?> list(@Validated @RequestBody CalenderQueryForm form) {
        QueryWrapper<CalendarEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "list");
        List<CalendarDTO> list = calendarService.listDto(queryWrapper);

        return new Result<>().success(list);
    }

    @PostMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sys:calendar:query")
    public Result<?> page(@Validated @RequestBody CalenderQueryForm form) {
        QueryWrapper<CalendarEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");
        PageData<CalendarDTO> page = calendarService.pageDto(form.getPage(), queryWrapper);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sys:calendar:query")
    public Result<?> info(@Validated @RequestBody CalenderDayDateForm form) {
        CalendarDTO data = calendarService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

}
