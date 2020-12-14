package com.nb6868.onexboot.api.modules.log.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReleaseExcel {

    @Excel(name = "android/ios/api/vue")
    private String type;
    @Excel(name = "编码")
    private String code;
    @Excel(name = "名称")
    private String name;
    @Excel(name = "版本号")
    private Integer versionNo;
    @Excel(name = "版本名称")
    private String versionName;
    @Excel(name = "更新记录")
    private String content;
    @Excel(name = "下载链接")
    private String downloadLink;
    @Excel(name = "强制更新")
    private Integer forceUpdate;
    @Excel(name = "显示在下载页面")
    private Integer show;

}
