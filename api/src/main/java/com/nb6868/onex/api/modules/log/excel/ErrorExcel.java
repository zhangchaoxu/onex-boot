package com.nb6868.onex.api.modules.log.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 异常日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class ErrorExcel {

    @Excel(name = "请求URI")
    private String uri;
    @Excel(name = "请求方式")
    private String method;
    @Excel(name = "请求参数")
    private String params;
    @Excel(name = "User-Agent")
    private String userAgent;
    @Excel(name = "操作IP")
    private String ip;
    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
