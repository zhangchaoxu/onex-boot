package com.nb6868.onex.sys.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.sys.dao.CalendarDao;
import com.nb6868.onex.sys.entity.CalendarEntity;
import com.nb6868.onex.common.jpa.EntityService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 万年历
 *
 * @author Charles zhangchaoxu@gmail.comc
 */
@Service
public class CalendarService extends EntityService<CalendarDao, CalendarEntity> {

    /**
     * 是否工作日
     */
    public boolean isWorkday(String day) {
        CalendarEntity calendarEntity = getOneByColumn("day_date", day);
        return calendarEntity != null && (calendarEntity.getType() == 0 || calendarEntity.getType() == 3);
    }

    /**
     * 用接口同步
     * http://timor.tech/api/holiday/
     */
    public boolean syncWithApi(Date startDay, Date endDay) {
        // 日期列表
        while (DateUtil.compare(startDay, endDay) <= 0) {
            // 调用接口,用jsoup,而不是RestTemplate是由于可能会被拦截403
            try {
                // 直接接口读取可能会被403,所以用jsoup模拟
                Connection.Response res = Jsoup.connect("http://timor.tech/api/holiday/info/" + DateUtil.format(startDay, "yyyy-MM-dd"))
                        .header("Accept", "*/*")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                        .timeout(3000000)
                        .ignoreContentType(true)
                        .execute();
                JSONObject jsonResult = JSONUtil.parseObj(res.body());
                JSONObject jsonType = jsonResult.getJSONObject("type");
                CalendarEntity calendar = new CalendarEntity();
                calendar.setWeek(jsonType.getInt("week"));
                // 节假日类型，分别表示 工作日、周末、节日、调休
                calendar.setType(jsonType.getInt("type"));
                JSONObject workholiday = jsonResult.getJSONObject("holiday");
                if (workholiday == null) {
                    // 如果不是节假日，holiday字段为null。
                    calendar.setWage(1);
                } else {
                    calendar.setWage(workholiday.getInt("wage"));
                    calendar.setHolidayName(workholiday.getStr("name"));
                }
                calendar.setDayDate(startDay);
                save(calendar);
            } catch (Exception e) {
                break;
            }
            startDay = DateUtil.offsetDay(startDay, 1);
        }
        return true;
    }

}
