package com.nb6868.onex.common.dingtalk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel(value = "调用结果,返回体")
@EqualsAndHashCode(callSuper = false)
public class ResultResponse<T> extends BaseResponse {

    @ApiModelProperty(value = "结果")
    private T result;

}
