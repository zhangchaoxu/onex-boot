package com.nb6868.onex.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 万年历
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_calendar")
public class CalendarEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键,日期
	 */
	@TableId(type = IdType.INPUT)
	private Date dayDate;

    /**
     * 类型:0工作日/1周末/2节日/3调休
     */
	private Integer type;
    /**
     * 星期
     */
	private Integer week;
	/**
	 * 薪资倍数
	 */
	private Integer wage;
	/**
	 * 节假日名称
	 */
	private String holidayName;
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

}
