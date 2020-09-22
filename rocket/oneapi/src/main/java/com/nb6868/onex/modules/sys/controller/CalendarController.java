package com.nb6868.onex.modules.sys.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.modules.sys.dto.CalendarDTO;
import com.nb6868.onex.modules.sys.service.CalendarService;
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

/**
 * 万年历
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("sys/calendar")
@Validated
@Api(tags = "万年历")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("sys:calendar:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<CalendarDTO> list = calendarService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sys:calendar:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<CalendarDTO> page = calendarService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sys:calendar:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        CalendarDTO data = calendarService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    /**
     * 与接口同步数据
     *
     * see {https://www.jisuapi.com/api/calendar/}
     */
    /*@PostMapping("sync")
    @ApiOperation("同步")
    @LogOperation("同步")
    @RequiresPermissions("sys:calendar:sync")
    public Result sync() {
        // 获取今年列表
        List<Date> dayList = new ArrayList<>();
        LocalDate firstDay = new LocalDate(new Date()).dayOfYear().withMinimumValue();
        LocalDate lastDay = new LocalDate(new Date()).plusYears(1).dayOfYear().withMinimumValue();
        while (firstDay.isBefore(lastDay)) {
            dayList.add(firstDay.toDateTimeAtStartOfDay().toDate());
            firstDay = firstDay.plusDays(1);
        }
        for (Date date : dayList) {
            Request request = new Request.Builder().url("https://api.jisuapi.com/calendar/query?appkey=bb07afa0f99a9c40&date=" + new SimpleDateFormat("yyyy-MM-dd").format(date)).build();

            Response response;
            try {
                response = new OkHttpClient().newCall(request).execute();
            } catch (IOException ioe) {
                throw new XException(ErrorCode.SEND_SMS_ERROR, ioe, "调用接口失败");
            }
            // 接口结果
            String result = "";
            if (response.isSuccessful()) {
                try {
                    assert response.body() != null;
                    result = response.body().string();
                } catch (IOException ioe) {
                    throw new OnexException(ErrorCode.SEND_SMS_ERROR, "接口返回数据异常");
                }
                JSONObject json = JSONObject.parseObject(result);
                if (json.getIntValue("status") != 0) {
                    throw new XException(ErrorCode.SEND_SMS_ERROR, json.getString("reason"));
                } else {
                    json = json.getJSONObject("result");
                    logger.info(result);
                    CalendarDTO calendar = new CalendarDTO();
                    calendar.setYear(json.getIntValue("year"));
                    calendar.setMonth(json.getString("month").startsWith("0") ? Integer.valueOf(json.getString("month").replaceFirst("0", "")) : Integer.valueOf(json.getString("month")));
                    calendar.setDay(json.getString("day").startsWith("0") ? Integer.valueOf(json.getString("day").replaceFirst("0", "")) : Integer.valueOf(json.getString("day")));
                    calendar.setGanzhi(json.getString("ganzhi"));
                    calendar.setLunarday(json.getString("lunarday"));
                    calendar.setLunarmonth(json.getString("lunarmonth"));
                    calendar.setShengxiao(json.getString("shengxiao"));
                    calendar.setLunaryear(json.getString("lunaryear"));
                    calendar.setStar(json.getString("star"));
                    //0 节假日调班，1节假日休息，2正常工作日，3正常周末
                    Integer workholiday = json.getInteger("workholiday");
                    if ("一".equalsIgnoreCase(json.getString("week"))) {
                        calendar.setWeek(1);
                    } else if ("二".equalsIgnoreCase(json.getString("week"))) {
                        calendar.setWeek(2);
                    } else if ("三".equalsIgnoreCase(json.getString("week"))) {
                        calendar.setWeek(3);
                    } else if ("四".equalsIgnoreCase(json.getString("week"))) {
                        calendar.setWeek(4);
                    } else if ("五".equalsIgnoreCase(json.getString("week"))) {
                        calendar.setWeek(5);
                    } else if ("六".equalsIgnoreCase(json.getString("week"))) {
                        calendar.setWeek(6);
                    } else if ("日".equalsIgnoreCase(json.getString("week"))) {
                        calendar.setWeek(7);
                    }
                    if (workholiday != null) {
                        calendar.setType(workholiday);
                    } else {
                        calendar.setType(calendar.getWeek() >= 6 ? 3 : 2);
                    }
                    try {
                        calendar.setDayDate(new SimpleDateFormat("yyyy-MM-dd").parse(json.getString("year") + "-" + json.getString("month") + "-" + json.getString("day")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendarService.insert(calendar);
                }
            }
        }

        return new Result();
    }*/

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:calendar:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody CalendarDTO dto) {
        calendarService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:calendar:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody CalendarDTO dto) {
        calendarService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:calendar:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        calendarService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:calendar:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        calendarService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
