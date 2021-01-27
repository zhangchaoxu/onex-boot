package com.nb6868.onexboot.api;

import com.nb6868.onexboot.api.modules.sys.dao.CalendarDao;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalendarTest {

    @Autowired
    CalendarDao calendarDao;

    private List<Date> getDayList() {
        // 日期列表
        List<Date> days = new ArrayList<>();
        LocalDate firstDay = new LocalDate(2010, 1, 1);
        LocalDate lastDay = new LocalDate(2013, 12, 31).plusDays(1);
        while (firstDay.isBefore(lastDay)) {
            days.add(firstDay.toDateTimeAtStartOfDay().toDate());
            firstDay = firstDay.plusDays(1);
        }
        return days;
    }

    /**
     * 与节假日接口同步数据
     * 目的是为了获得节假日和薪资倍数的数据
     * see {https://www.jisuapi.com/api/calendar/}
     * see {http://timor.tech/api/holiday}
     */
    @Test
    public void syncHoliday() {
        // 先把原先的删除了
        List<Date> dayList = getDayList();
        RestTemplate restTemplate = new RestTemplate();
        for (Date date : dayList) {
            /*JSONObject json;
            try {
                json = restTemplate.getForObject("http://timor.tech/api/holiday/info/{1}/", JSONObject.class, new SimpleDateFormat("yyyy-MM-dd").format(date));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("接口调用异常:" + e.getMessage());
                break;
            }

            JSONObject jsonType = json.getJSONObject("type");
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            CalendarEntity calendar = new CalendarEntity();
            calendar.setYear(c.get(Calendar.YEAR));
            calendar.setMonth(c.get(Calendar.MONTH) + 1);
            calendar.setDay(c.get(Calendar.DATE));
            calendar.setWeek(jsonType.getIntValue("week"));
            calendar.setType(jsonType.getIntValue("type"));

            //0 节假日调班，1节假日休息，2正常工作日，3正常周末
            JSONObject workholiday = json.getJSONObject("holiday");
            if (workholiday == null) {
                // 如果不是节假日，holiday字段为null。
                calendar.setWage(1);
            } else {
                calendar.setWage(workholiday.getIntValue("wage"));
                calendar.setHolidayName(workholiday.getString("name"));
            }
            calendar.setDayDate(date);
            calendarDao.insert(calendar);
            System.out.println("count:" + calendarDao.selectCount(new QueryWrapper<>()));*/
        }
    }

}
