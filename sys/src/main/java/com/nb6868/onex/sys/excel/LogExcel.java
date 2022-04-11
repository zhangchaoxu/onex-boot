package com.nb6868.onex.sys.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class LogExcel {

    @Excel(name = "类型")
    private String type;
    @Excel(name = "内容")
    private String content;
    @Excel(name = "用户")
    private String createName;
    @Excel(name = "操作")
    private String operation;
    @Excel(name = "请求URI")
    private String uri;
    @Excel(name = "请求方式")
    private String method;
    @Excel(name = "请求参数")
    private String params;
    @Excel(name = "耗时(毫秒)")
    private Long requestTime;
    @Excel(name = "UA")
    private String userAgent;
    @Excel(name = "IP")
    private String ip;
    @Excel(name = "状态", replace = {"失败_0", "成功_1"})
    private Integer state;
    @Excel(name = "操作时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
