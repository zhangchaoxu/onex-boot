package com.nb6868.onex.api.modules.log.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 登录日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class LoginExcel {
    @Excel(name = "类型")
    private String type;
    @Excel(name = "结果", replace = {"失败_0","成功_1"})
    private Integer result;
    @Excel(name = "消息")
    private Integer msg;
    @Excel(name = "User-Agent")
    private String userAgent;
    @Excel(name = "操作IP")
    private String ip;
    @Excel(name = "用户名")
    private String createName;
    @Excel(name = "登录时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
