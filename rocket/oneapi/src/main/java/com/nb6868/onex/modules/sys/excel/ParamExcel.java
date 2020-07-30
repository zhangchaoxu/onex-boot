package com.nb6868.onex.modules.sys.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 参数管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class ParamExcel {

    @Excel(name = "参数编码")
    private String code;
    @Excel(name = "参数值")
    private String value;
    @Excel(name = "备注")
    private String remark;

}
