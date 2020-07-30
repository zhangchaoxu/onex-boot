package com.nb6868.onex.modules.ba.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秉奥-检测题
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SubjectExcel {

    @Excel(name = "类型", replace = {"成人检测_1", "孩子检测_2"})
    private Integer type;
    @Excel(name = "题目")
    private String question;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "选项")
    private String options;
    @Excel(name = "答案")
    private String answer;

}
