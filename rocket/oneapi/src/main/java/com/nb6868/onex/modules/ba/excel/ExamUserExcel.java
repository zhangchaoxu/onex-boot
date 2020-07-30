package com.nb6868.onex.modules.ba.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 秉奥-用户检测
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExamUserExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "用户id")
    private Long userId;
    @Excel(name = "检测类型 1 成人检测 2 孩子检测")
    private Integer subjectType;
    @Excel(name = "检测结果")
    private String result;
    @Excel(name = "家长名字")
    private String parentName;
    @Excel(name = "小孩名字")
    private String childName;
    @Excel(name = "小孩年级")
    private String childClass;
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
