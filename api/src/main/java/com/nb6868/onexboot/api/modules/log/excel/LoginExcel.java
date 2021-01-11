package com.nb6868.onexboot.api.modules.log.excel;

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
    @Excel(name = "类型", replace = {"后台退出_-10", "APP退出_-50",
            "后台帐号密码登录_10", "后台手机密码登录_20", "后台手机短信登录_30", "后台微信登录_40",
            "APP帐号密码登录_50", "APP手机密码登录_60", "APP手机短信登录_70", "APP微信登录_80"})
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
