package com.nb6868.onex.common;

import cn.hutool.core.date.DateUtil;
import com.nb6868.onex.common.util.HolidayUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("节假日测试")
@Slf4j
public class HolidayTest {

    @Test
    @DisplayName("测试")
    void testHoliday () {
        log.error("" + HolidayUtils.isHoliday(DateUtil.parse("2025-01-01", "yyyy-MM-dd")));
        log.error("" + HolidayUtils.isHoliday(DateUtil.parse("2025-02-08", "yyyy-MM-dd")));
    }
}
