package com.nb6868.onex.sys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "万年历")
public class CalendarDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "日期")
	private Date dayDate;

	@ApiModelProperty(value = "类型:0工作日/1周末/2节日/3调休")
	private Integer type;

	@ApiModelProperty(value = "星期")
	private Integer week;

	@ApiModelProperty(value = "农历年")
	private String lunaryear;

	@ApiModelProperty(value = "农历月")
	private String lunarmonth;

	@ApiModelProperty(value = "农历日")
	private String lunarday;

	@ApiModelProperty(value = "生肖")
	private String shengxiao;

	@ApiModelProperty(value = "干支")
	private String ganzhi;

	@ApiModelProperty(value = "星座")
	private String star;

}
