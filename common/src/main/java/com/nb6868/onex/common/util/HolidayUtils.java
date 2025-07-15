package com.nb6868.onex.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 节假日判断工具
 * 支持2013年-2025年
 *
 * ref <a href="https://mp.weixin.qq.com/s/fw7PylKEFZhAjHBL9GvRcA">...</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class HolidayUtils {

    // 法定节假日集合
    private static final Set<Date> HOLIDAYS = new HashSet<>();

    // 调休工作日集合（周末调休为工作日）
    private static final Set<Date> ADJUSTED_WORKDAYS = new HashSet<>();

    static {
        // 2025
        HOLIDAYS.add(DateUtil.parse("2025-01-01", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2025-01-26", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2025-01-28", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2025-01-29", "yyyy-MM-dd"));  // 初一
        HOLIDAYS.add(DateUtil.parse("2025-01-30", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2025-01-31", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2025-02-01", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2025-02-02", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2025-02-03", "yyyy-MM-dd"));  // 初六
        HOLIDAYS.add(DateUtil.parse("2025-02-04", "yyyy-MM-dd"));  // 初七
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2025-02-08", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2025-04-04", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2025-04-05", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2025-04-06", "yyyy-MM-dd"));  // 清明节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2025-04-27", "yyyy-MM-dd"));  // 劳动节前补班
        HOLIDAYS.add(DateUtil.parse("2025-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2025-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2025-05-03", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2025-05-04", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2025-05-05", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2025-05-31", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2025-06-01", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2025-06-02", "yyyy-MM-dd"));  // 端午节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2025-09-28", "yyyy-MM-dd"));  // 国庆节前补班
        HOLIDAYS.add(DateUtil.parse("2025-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2025-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2025-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2025-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2025-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2025-10-06", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2025-10-07", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2025-10-08", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2025-10-11", "yyyy-MM-dd"));  // 国庆节后补班
        // 2024
        HOLIDAYS.add(DateUtil.parse("2024-01-01", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2024-02-04", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2024-02-10", "yyyy-MM-dd"));  // 初一
        HOLIDAYS.add(DateUtil.parse("2024-02-11", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2024-02-12", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2024-02-13", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2024-02-14", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2024-02-15", "yyyy-MM-dd"));  // 初六
        HOLIDAYS.add(DateUtil.parse("2024-02-16", "yyyy-MM-dd"));  // 初七
        HOLIDAYS.add(DateUtil.parse("2024-02-17", "yyyy-MM-dd"));  // 初八
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2024-02-18", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2024-04-04", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2024-04-05", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2024-04-06", "yyyy-MM-dd"));  // 清明节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2024-04-07", "yyyy-MM-dd"));  // 清明节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2024-04-28", "yyyy-MM-dd"));  // 劳动节前补班
        HOLIDAYS.add(DateUtil.parse("2024-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2024-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2024-05-03", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2024-05-04", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2024-05-05", "yyyy-MM-dd"));  // 劳动节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2024-05-11", "yyyy-MM-dd"));  // 劳动节后补班
        HOLIDAYS.add(DateUtil.parse("2024-06-08", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2024-06-09", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2024-06-10", "yyyy-MM-dd"));  // 端午节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2024-09-14", "yyyy-MM-dd"));  // 中秋节前补班
        HOLIDAYS.add(DateUtil.parse("2024-09-15", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2024-09-16", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2024-09-17", "yyyy-MM-dd"));  // 中秋节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2024-09-29", "yyyy-MM-dd"));  // 国庆节前补班
        HOLIDAYS.add(DateUtil.parse("2024-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2024-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2024-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2024-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2024-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2024-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2024-10-07", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2024-10-12", "yyyy-MM-dd"));  // 国庆节后补班
        // 2023
        HOLIDAYS.add(DateUtil.parse("2023-01-01", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2023-01-02", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2023-01-21", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2023-01-22", "yyyy-MM-dd"));  // 初一
        HOLIDAYS.add(DateUtil.parse("2023-01-23", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2023-01-24", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2023-01-25", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2023-01-26", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2023-01-27", "yyyy-MM-dd"));  // 初六
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2023-01-28", "yyyy-MM-dd"));  // 春节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2023-01-29", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2023-04-05", "yyyy-MM-dd"));  // 清明节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2023-04-23", "yyyy-MM-dd"));  // 劳动节前补班
        HOLIDAYS.add(DateUtil.parse("2023-04-29", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2023-04-30", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2023-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2023-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2023-05-03", "yyyy-MM-dd"));  // 劳动节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2023-05-06", "yyyy-MM-dd"));  // 劳动节后补班
        HOLIDAYS.add(DateUtil.parse("2023-06-22", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2023-06-23", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2023-06-24", "yyyy-MM-dd"));  // 端午节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2023-06-25", "yyyy-MM-dd"));  // 端午节后补班
        HOLIDAYS.add(DateUtil.parse("2023-09-29", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2023-09-30", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2023-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2023-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2023-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2023-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2023-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2023-10-06", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2023-10-07", "yyyy-MM-dd"));  // 国庆节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2023-10-08", "yyyy-MM-dd"));  // 国庆节后补班
        HOLIDAYS.add(DateUtil.parse("2023-12-30", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2023-12-31", "yyyy-MM-dd"));  // 元旦
        // 2022
        HOLIDAYS.add(DateUtil.parse("2022-01-01", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2022-01-02", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2022-01-03", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2022-01-29", "yyyy-MM-dd"));  // 春节前补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2022-01-30", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2022-01-31", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2022-02-01", "yyyy-MM-dd"));  // 初一
        HOLIDAYS.add(DateUtil.parse("2022-02-02", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2022-02-03", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2022-02-04", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2022-02-05", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2022-02-06", "yyyy-MM-dd"));  // 初六
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2022-04-02", "yyyy-MM-dd"));  // 清明节前补班
        HOLIDAYS.add(DateUtil.parse("2022-04-03", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2022-04-04", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2022-04-05", "yyyy-MM-dd"));  // 清明节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2022-04-24", "yyyy-MM-dd"));  // 劳动节前补班
        HOLIDAYS.add(DateUtil.parse("2022-04-30", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2022-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2022-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2022-05-03", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2022-05-04", "yyyy-MM-dd"));  // 劳动节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2022-05-07", "yyyy-MM-dd"));  // 劳动节后补班
        HOLIDAYS.add(DateUtil.parse("2022-06-03", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2022-06-04", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2022-06-05", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2022-09-10", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2022-09-11", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2022-09-12", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2022-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2022-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2022-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2022-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2022-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2022-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2022-10-07", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2022-10-08", "yyyy-MM-dd"));  // 国庆节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2022-10-09", "yyyy-MM-dd"));  // 国庆节后补班
        HOLIDAYS.add(DateUtil.parse("2022-12-31", "yyyy-MM-dd"));  // 元旦
        // 2021
        HOLIDAYS.add(DateUtil.parse("2021-01-01", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2021-01-02", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2021-01-03", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2021-02-07", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2021-02-11", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2021-02-12", "yyyy-MM-dd"));  // 初一
        HOLIDAYS.add(DateUtil.parse("2021-02-13", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2021-02-14", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2021-02-15", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2021-02-16", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2021-02-17", "yyyy-MM-dd"));  // 初六
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2021-02-20", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2021-04-03", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2021-04-04", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2021-04-05", "yyyy-MM-dd"));  // 清明节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2021-04-25", "yyyy-MM-dd"));  // 劳动节前补班
        HOLIDAYS.add(DateUtil.parse("2021-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2021-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2021-05-03", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2021-05-04", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2021-05-05", "yyyy-MM-dd"));  // 劳动节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2021-05-08", "yyyy-MM-dd"));  // 劳动节后补班
        HOLIDAYS.add(DateUtil.parse("2021-06-12", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2021-06-13", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2021-06-14", "yyyy-MM-dd"));  // 端午节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2021-09-18", "yyyy-MM-dd"));  // 中秋节前补班
        HOLIDAYS.add(DateUtil.parse("2021-09-19", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2021-09-20", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2021-09-21", "yyyy-MM-dd"));  // 中秋节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2021-09-26", "yyyy-MM-dd"));  // 国庆节前补班
        HOLIDAYS.add(DateUtil.parse("2021-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2021-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2021-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2021-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2021-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2021-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2021-10-07", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2021-10-09", "yyyy-MM-dd"));  // 国庆节后补班
        // 2020
        HOLIDAYS.add(DateUtil.parse("2020-01-01", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2020-01-19", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2020-01-24", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2020-01-25", "yyyy-MM-dd"));  // 初一
        HOLIDAYS.add(DateUtil.parse("2020-01-26", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2020-01-27", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2020-01-28", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2020-01-29", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2020-01-30", "yyyy-MM-dd"));  // 初六
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2020-02-01", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2020-04-04", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2020-04-05", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2020-04-06", "yyyy-MM-dd"));  // 清明节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2020-04-26", "yyyy-MM-dd"));  // 劳动节前补班
        HOLIDAYS.add(DateUtil.parse("2020-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2020-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2020-05-03", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2020-05-04", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2020-05-05", "yyyy-MM-dd"));  // 劳动节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2020-05-09", "yyyy-MM-dd"));  // 劳动节后补班
        HOLIDAYS.add(DateUtil.parse("2020-06-25", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2020-06-26", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2020-06-27", "yyyy-MM-dd"));  // 端午节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2020-06-28", "yyyy-MM-dd"));  // 端午节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2020-09-27", "yyyy-MM-dd"));  // 国庆节前补班
        HOLIDAYS.add(DateUtil.parse("2020-10-01", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2020-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2020-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2020-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2020-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2020-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2020-10-07", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2020-10-08", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2020-10-10", "yyyy-MM-dd"));  // 国庆节后补班
        // 2019
        HOLIDAYS.add(DateUtil.parse("2019-01-01", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2019-02-02", "yyyy-MM-dd"));  // 春节前补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2019-02-03", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2019-02-04", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2019-02-05", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2019-02-06", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2019-02-07", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2019-02-08", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2019-02-09", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2019-02-10", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2019-04-05", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2019-04-06", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2019-04-07", "yyyy-MM-dd"));  // 清明节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2019-04-28", "yyyy-MM-dd"));  // 劳动节前补班
        HOLIDAYS.add(DateUtil.parse("2019-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2019-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2019-05-03", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2019-05-04", "yyyy-MM-dd"));  // 劳动节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2019-05-05", "yyyy-MM-dd"));  // 劳动节后补班
        HOLIDAYS.add(DateUtil.parse("2019-06-07", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2019-06-08", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2019-06-09", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2019-09-13", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2019-09-14", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2019-09-15", "yyyy-MM-dd"));  // 中秋节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2019-09-29", "yyyy-MM-dd"));  // 国庆节前补班
        HOLIDAYS.add(DateUtil.parse("2019-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2019-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2019-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2019-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2019-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2019-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2019-10-07", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2019-10-12", "yyyy-MM-dd"));  // 国庆节后补班
        // 2018
        HOLIDAYS.add(DateUtil.parse("2018-01-01", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2018-02-11", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2018-02-15", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2018-02-16", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2018-02-17", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2018-02-18", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2018-02-19", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2018-02-20", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2018-02-21", "yyyy-MM-dd"));  // 春节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2018-02-24", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2018-04-05", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2018-04-06", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2018-04-07", "yyyy-MM-dd"));  // 清明节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2018-04-08", "yyyy-MM-dd"));  // 清明节补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2018-04-28", "yyyy-MM-dd"));  // 劳动节前补班
        HOLIDAYS.add(DateUtil.parse("2018-04-29", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2018-04-30", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2018-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2018-06-16", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2018-06-17", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2018-06-18", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2018-09-22", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2018-09-23", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2018-09-24", "yyyy-MM-dd"));  // 中秋节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2018-09-29", "yyyy-MM-dd"));  // 国庆前补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2018-09-30", "yyyy-MM-dd"));  // 国庆前补班
        HOLIDAYS.add(DateUtil.parse("2018-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2018-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2018-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2018-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2018-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2018-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2018-10-07", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2018-12-29", "yyyy-MM-dd"));  // 元旦前补班
        HOLIDAYS.add(DateUtil.parse("2018-12-30", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2018-12-31", "yyyy-MM-dd"));  // 元旦
        // 2017
        HOLIDAYS.add(DateUtil.parse("2017-01-01", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2017-01-02", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2017-01-22", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2017-01-27", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2017-01-28", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2017-01-29", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2017-01-30", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2017-01-31", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2017-02-01", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2017-02-02", "yyyy-MM-dd"));  // 春节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2017-02-04", "yyyy-MM-dd"));  // 春节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2017-04-01", "yyyy-MM-dd"));  // 清明前补班
        HOLIDAYS.add(DateUtil.parse("2017-04-02", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2017-04-03", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2017-04-04", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2017-04-29", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2017-04-30", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2017-05-01", "yyyy-MM-dd"));  // 劳动节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2017-05-27", "yyyy-MM-dd"));  // 端午节前补班
        HOLIDAYS.add(DateUtil.parse("2017-05-28", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2017-05-29", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2017-05-30", "yyyy-MM-dd"));  // 端午节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2017-09-30", "yyyy-MM-dd"));  // 中秋和国庆前补班
        HOLIDAYS.add(DateUtil.parse("2017-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2017-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2017-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2017-10-04", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2017-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2017-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2017-10-07", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2017-10-08", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2017-12-30", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2017-12-31", "yyyy-MM-dd"));  // 元旦
        // 2016
        HOLIDAYS.add(DateUtil.parse("2016-01-01", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2016-01-02", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2016-01-03", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2016-02-06", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2016-02-07", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2016-02-08", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2016-02-09", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2016-02-10", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2016-02-11", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2016-02-12", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2016-02-13", "yyyy-MM-dd"));  // 初六
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2016-02-14", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2016-04-02", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2016-04-03", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2016-04-04", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2016-04-30", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2016-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2016-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2016-06-09", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2016-06-10", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2016-06-11", "yyyy-MM-dd"));  // 端午节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2016-06-12", "yyyy-MM-dd"));  // 端午节后补班
        HOLIDAYS.add(DateUtil.parse("2016-09-15", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2016-09-16", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2016-09-17", "yyyy-MM-dd"));  // 中秋节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2016-09-18", "yyyy-MM-dd"));  // 中秋节后补班
        HOLIDAYS.add(DateUtil.parse("2016-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2016-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2016-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2016-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2016-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2016-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2016-10-07", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2016-10-08", "yyyy-MM-dd"));  // 国庆节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2016-10-09", "yyyy-MM-dd"));  // 国庆节后补班
        HOLIDAYS.add(DateUtil.parse("2016-12-31", "yyyy-MM-dd"));  // 元旦
        // 2015
        HOLIDAYS.add(DateUtil.parse("2015-01-01", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2015-01-02", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2015-01-03", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2015-01-04", "yyyy-MM-dd"));  // 元旦后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2015-02-15", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2015-02-18", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2015-02-19", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2015-02-20", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2015-02-21", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2015-02-22", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2015-02-23", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2015-02-24", "yyyy-MM-dd"));  // 初六
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2015-02-28", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2015-04-04", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2015-04-05", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2015-04-06", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2015-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2015-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2015-05-03", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2015-06-20", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2015-06-21", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2015-06-22", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2015-09-03", "yyyy-MM-dd"));  // 抗战胜利日
        HOLIDAYS.add(DateUtil.parse("2015-09-04", "yyyy-MM-dd"));  // 抗战胜利日
        HOLIDAYS.add(DateUtil.parse("2015-09-05", "yyyy-MM-dd"));  // 抗战胜利日
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2015-09-06", "yyyy-MM-dd"));  // 抗战胜利日后补班
        HOLIDAYS.add(DateUtil.parse("2015-09-26", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2015-09-27", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2015-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2015-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2015-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2015-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2015-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2015-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2015-10-07", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2015-10-10", "yyyy-MM-dd"));  // 国庆节后补班
        // 2014
        HOLIDAYS.add(DateUtil.parse("2014-01-01", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2014-01-26", "yyyy-MM-dd"));  // 春节前补班
        HOLIDAYS.add(DateUtil.parse("2014-01-31", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2014-02-01", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2014-02-02", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2014-02-03", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2014-02-04", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2014-02-05", "yyyy-MM-dd"));  // 初六
        HOLIDAYS.add(DateUtil.parse("2014-02-06", "yyyy-MM-dd"));  // 初七
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2014-02-08", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2014-04-05", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2014-04-06", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2014-04-07", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2014-05-01", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2014-05-02", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2014-05-03", "yyyy-MM-dd"));  // 劳动节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2014-05-04", "yyyy-MM-dd"));  // 劳动节后补班
        HOLIDAYS.add(DateUtil.parse("2014-05-31", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2014-06-01", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2014-06-02", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2014-09-06", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2014-09-07", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2014-09-08", "yyyy-MM-dd"));  // 中秋节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2014-09-28", "yyyy-MM-dd"));  // 国庆节前补班
        HOLIDAYS.add(DateUtil.parse("2014-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2014-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2014-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2014-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2014-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2014-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2014-10-07", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2014-10-11", "yyyy-MM-dd"));  // 国庆节后补班
        // 2013
        HOLIDAYS.add(DateUtil.parse("2013-01-01", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2013-01-02", "yyyy-MM-dd"));  // 元旦
        HOLIDAYS.add(DateUtil.parse("2013-01-03", "yyyy-MM-dd"));  // 元旦
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-01-05", "yyyy-MM-dd"));  // 元旦后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-01-06", "yyyy-MM-dd"));  // 元旦后补班
        HOLIDAYS.add(DateUtil.parse("2013-02-09", "yyyy-MM-dd"));  // 除夕
        HOLIDAYS.add(DateUtil.parse("2013-02-10", "yyyy-MM-dd"));  // 春节
        HOLIDAYS.add(DateUtil.parse("2013-02-11", "yyyy-MM-dd"));  // 初二
        HOLIDAYS.add(DateUtil.parse("2013-02-12", "yyyy-MM-dd"));  // 初三
        HOLIDAYS.add(DateUtil.parse("2013-02-13", "yyyy-MM-dd"));  // 初四
        HOLIDAYS.add(DateUtil.parse("2013-02-14", "yyyy-MM-dd"));  // 初五
        HOLIDAYS.add(DateUtil.parse("2013-02-15", "yyyy-MM-dd"));  // 初六
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-02-16", "yyyy-MM-dd"));  // 春节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-02-17", "yyyy-MM-dd"));  // 春节后补班
        HOLIDAYS.add(DateUtil.parse("2013-04-04", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2013-04-05", "yyyy-MM-dd"));  // 清明节
        HOLIDAYS.add(DateUtil.parse("2013-04-06", "yyyy-MM-dd"));  // 清明节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-04-07", "yyyy-MM-dd"));  // 清明节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-04-27", "yyyy-MM-dd"));  // 劳动节前补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-04-28", "yyyy-MM-dd"));  // 劳动节前补班
        HOLIDAYS.add(DateUtil.parse("2013-04-29", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2013-04-30", "yyyy-MM-dd"));  // 劳动节
        HOLIDAYS.add(DateUtil.parse("2013-05-01", "yyyy-MM-dd"));  // 劳动节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-06-08", "yyyy-MM-dd"));  // 端午节前补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-06-09", "yyyy-MM-dd"));  // 端午节前补班
        HOLIDAYS.add(DateUtil.parse("2013-06-10", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2013-06-11", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2013-06-12", "yyyy-MM-dd"));  // 端午节
        HOLIDAYS.add(DateUtil.parse("2013-09-19", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2013-09-20", "yyyy-MM-dd"));  // 中秋节
        HOLIDAYS.add(DateUtil.parse("2013-09-21", "yyyy-MM-dd"));  // 中秋节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-09-22", "yyyy-MM-dd"));  // 中秋节后补班
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-09-29", "yyyy-MM-dd"));  // 国庆节前补班
        HOLIDAYS.add(DateUtil.parse("2013-10-01", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2013-10-02", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2013-10-03", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2013-10-04", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2013-10-05", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2013-10-06", "yyyy-MM-dd"));  // 国庆节
        HOLIDAYS.add(DateUtil.parse("2013-10-07", "yyyy-MM-dd"));  // 国庆节
        ADJUSTED_WORKDAYS.add(DateUtil.parse("2013-10-12", "yyyy-MM-dd"));  // 国庆节后补班
    }

    /**
     * 判断日期是否为周中日期
     */
    public static boolean isWeekday(Date date) {
        return !DateUtil.isWeekend(date);
    }

    /**
     * 判断日期是否为周中日期
     */
    public static boolean isWeekend(Date date) {
        return DateUtil.isWeekend(date);
    }

    /**
     * 判断日期是否为法定节假日
     */
    public static boolean isHoliday(Date date) {
        return HOLIDAYS.contains(date);
    }

    /**
     * 判断日期是否为调休工作日
     */
    public static boolean isAdjustedWorkday(Date date) {
        return ADJUSTED_WORKDAYS.contains(date);
    }

    /**
     * 判断日期是否为需要上班的日子（工作日或调休工作日）
     */
    public static boolean isWorkingDay(Date date) {
        // 如果是法定节假日，不是工作日
        if (isHoliday(date)) {
            return false;
        }

        // 如果是调休工作日，是工作日
        if (isAdjustedWorkday(date)) {
            return true;
        }

        // 否则根据周几判断
        return isWeekday(date);
    }

    public static void main(String[] args) {
        for (int year = 2022; year >= 2013; year--) {
            String result = HttpUtil.get("https://timor.tech/api/holiday/year/" + year);
            JSONObject holiday = JSONUtil.parseObj(result).getJSONObject("holiday");
            log.error("// " + year);
            holiday.forEach(new BiConsumer<String, Object>() {
                @Override
                public void accept(String key, Object value) {
                    JSONObject item = (JSONObject) value;
                    if (item.getBool("holiday")) {
                        log.error("HOLIDAYS.add(DateUtil.parse(\"" + item.getStr("date") + "\", \"yyyy-MM-dd\"));  // " + item.getStr("name"));
                    } else {
                        log.error("ADJUSTED_WORKDAYS.add(DateUtil.parse(\"" + item.getStr("date") + "\", \"yyyy-MM-dd\"));  // " + item.getStr("name"));
                    }
                }
            });
            ThreadUtil.sleep(30000);
        }
    }

}
