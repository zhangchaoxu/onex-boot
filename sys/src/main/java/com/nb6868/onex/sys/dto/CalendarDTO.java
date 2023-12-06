package com.nb6868.onex.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "万年历")
public class CalendarDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	 @Schema(description = "日期")
	private Date dayDate;

	 @Schema(description = "类型:0工作日/1周末/2节日/3调休")
	private Integer type;

	 @Schema(description = "星期")
	private Integer week;

	 @Schema(description = "农历年")
	private String lunaryear;

	 @Schema(description = "农历月")
	private String lunarmonth;

	 @Schema(description = "农历日")
	private String lunarday;

	 @Schema(description = "生肖")
	private String shengxiao;

	 @Schema(description = "干支")
	private String ganzhi;

	 @Schema(description = "星座")
	private String star;

}
