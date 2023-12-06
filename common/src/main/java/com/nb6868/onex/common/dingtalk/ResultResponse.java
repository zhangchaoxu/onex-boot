package com.nb6868.onex.common.dingtalk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "调用结果,返回体")
@EqualsAndHashCode(callSuper = false)
public class ResultResponse<T> extends BaseResponse {

     @Schema(description = "结果")
    private T result;

}
