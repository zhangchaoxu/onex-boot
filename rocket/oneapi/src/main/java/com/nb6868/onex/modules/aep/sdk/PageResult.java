package com.nb6868.onex.modules.aep.sdk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class PageResult<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前页码")
    private int pageNum;

    @ApiModelProperty(value = "每页记录数")
    private int pageSize;

    @ApiModelProperty(value = "总记录数")
    private long total;

    @ApiModelProperty(value = "是否还有下一页")
    public boolean hasNextPage() {
        return pageNum > 0 && pageSize > 0 && pageNum * pageSize < total;
    }

    @ApiModelProperty(value = "数据")
    private List<E> list;

}
