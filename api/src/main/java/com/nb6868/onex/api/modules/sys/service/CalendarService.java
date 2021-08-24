package com.nb6868.onex.api.modules.sys.service;

import com.nb6868.onex.api.modules.sys.dao.CalendarDao;
import com.nb6868.onex.api.modules.sys.entity.CalendarEntity;
import com.nb6868.onex.common.service.EntityService;
import org.springframework.stereotype.Service;

/**
 * 万年历
 *
 * @author Charles zhangchaoxu@gmail.comc
 */
@Service
public class CalendarService extends EntityService<CalendarDao, CalendarEntity> {

    /*@Override
    public QueryWrapper<CalendarEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<CalendarEntity>(new QueryWrapper<>(), params)
                .eq("year", "year")
                .eq("month", "month")
                .eq("day", "day")
                .eq("type", "type")
                .ge("day_date", "startDayDate")
                .le("day_date", "endDayDate")
                .getQueryWrapper();
    }*/

}
