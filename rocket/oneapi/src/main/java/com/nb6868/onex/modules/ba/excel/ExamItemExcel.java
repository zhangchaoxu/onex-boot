package com.nb6868.onex.modules.ba.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 秉奥-用户检测细项
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExamItemExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "用户id")
    private Long userId;
    @Excel(name = "检测id")
    private Long testId;
    @Excel(name = "题面")
    private String subjectQuestion;
    @Excel(name = "选中结果")
    private String subjectOption;
    @Excel(name = "选中结果答案")
    private String subjectAnswer;
    @Excel(name = "创建者")
    private Long createId;
    @Excel(name = "创建时间")
    private Date createTime;
    @Excel(name = "更新者")
    private Long updateId;
    @Excel(name = "更新时间")
    private Date updateTime;
    @Excel(name = "删除标记")
    private Integer deleted;

}
