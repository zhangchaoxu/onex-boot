package com.nb6868.onexboot.api.modules.sys.controller;

import com.nb6868.onexboot.api.modules.sys.dto.CalendarDTO;
import com.nb6868.onexboot.api.modules.sys.entity.CalendarEntity;
import com.nb6868.onexboot.api.modules.sys.service.CalendarService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.validator.AssertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 万年历
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/sys/calendar")
@Validated
@Api(tags = "万年历")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("sys:calendar:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<CalendarEntity> list = calendarService.listByMap(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sys:calendar:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<CalendarDTO> page = null;//calendarService.pageMaps(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sys:calendar:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Date id) {
        CalendarEntity data = calendarService.getById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

}
