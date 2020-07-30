package com.nb6868.onex.modules.sys.entity;

import com.nb6868.onex.booster.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 万年历
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_calendar")
public class CalendarEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 年
     */
	private Integer year;
    /**
     * 月
     */
	private Integer month;
    /**
     * 日
     */
	private Integer day;
    /**
     * 类型:
0 节假日调班，1节假日休息，2正常工作日，3正常周末
     */
	private Integer type;
    /**
     * 星期
     */
	private Integer week;
    /**
     * 农历年
     */
	private String lunaryear;
    /**
     * 农历月
     */
	private String lunarmonth;
    /**
     * 农历日
     */
	private String lunarday;
    /**
     * 生肖
     */
	private String shengxiao;
    /**
     * 干支
     */
	private String ganzhi;
    /**
     * 星座
     */
	private String star;
    /**
     * 日期
     */
	private Date dayDate;
}
