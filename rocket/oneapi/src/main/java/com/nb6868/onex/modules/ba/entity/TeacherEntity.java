package com.nb6868.onex.modules.ba.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秉奥-教师
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ba_teacher")
public class TeacherEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 头像
     */
    private String imgs;
    /**
     * 内容
     */
    private String content;
    /**
     * 状态0 未激活 1 激活
     */
    private Integer status;
}
