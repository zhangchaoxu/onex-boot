package com.nb6868.onex.common.pojo;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

     @Schema(description = "当前页码")
    private long pageNo;

     @Schema(description = "每页显示记录数")
    private long pageSize;

     @Schema(description = "是否最后页")
    private boolean lastPage = true;

     @Schema(description = "总记录数")
    private long total;

     @Schema(description = "列表数据")
    private List<T> list;

    /**
     * 构造方法
     */
    public PageData(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    /**
     * 构造方法
     */
    public PageData(IPage<T> iPage) {
        this.list = iPage.getRecords();
        this.total = iPage.getTotal();
        this.pageNo = iPage.getCurrent();
        this.pageSize = iPage.getSize();
        this.lastPage = iPage.getSize() * iPage.getCurrent() >= iPage.getTotal();
    }

}
