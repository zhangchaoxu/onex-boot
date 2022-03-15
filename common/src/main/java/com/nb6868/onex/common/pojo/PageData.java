package com.nb6868.onex.common.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@NoArgsConstructor
public class PageData<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前页码")
    private long pageNo;

    @ApiModelProperty(value = "每页显示记录数")
    private long pageSize;

    @ApiModelProperty(value = "是否最后页")
    private boolean lastPage = true;

    @ApiModelProperty(value = "总记录数")
    private long total;

    @ApiModelProperty(value = "列表数据")
    private List<T> list;

    /**
     * 分页
     * @param list  列表数据
     * @param total 总记录数
     */
    public PageData(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

}
