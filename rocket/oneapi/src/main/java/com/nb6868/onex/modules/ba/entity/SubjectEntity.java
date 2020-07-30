package com.nb6868.onex.modules.ba.entity;

import com.nb6868.onex.booster.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秉奥-检测题
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ba_subject")
public class SubjectEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 题面
     */
    private String question;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 1 成人检测 2 孩子检测
     */
    private Integer type;
    /**
     * 答案对应内容
     */
    private String answer;
    /**
     * 答案选项
     */
    private String options;
    /**
     * 答案解析
     */
    private String analysis;
    /**
     * 状态0 未启用 1 启用
     */
    private Integer status;
}
