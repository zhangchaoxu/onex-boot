package com.nb6868.onex.common.async;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 异步任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class AsyncTaskInfo {

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("状态")
    private AsyncTaskStatusEnum status;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("耗时")
    private Long times;

}
