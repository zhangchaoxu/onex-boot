package com.nb6868.onex.msg.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息记录查询")
public class MsgLogQueryForm extends BasePageForm {

    @Query
    @ApiModelProperty("编码")
    private String code;

    @Query
    @ApiModelProperty("类型")
    private Integer type;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column="name,title,content")
    @ApiModelProperty("关键词搜索")
    private String search;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

}
