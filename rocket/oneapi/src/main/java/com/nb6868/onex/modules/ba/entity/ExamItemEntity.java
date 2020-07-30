package com.nb6868.onex.modules.ba.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秉奥-用户检测细项
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ba_exam_item")
public class ExamItemEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 检测id
     */
    private Long testId;
    /**
     * 题面
     */
    private String subjectQuestion;
    /**
     * 选中结果
     */
    private String subjectOption;
    /**
     * 选中结果答案
     */
    private String subjectAnswer;
}
