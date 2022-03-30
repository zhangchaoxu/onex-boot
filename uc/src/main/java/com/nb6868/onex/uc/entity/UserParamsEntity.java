package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 用户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_user_params")
@Alias("uc_user_params")
public class UserParamsEntity extends BaseEntity {

    /**
     * 编码
     */
    private String code;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 内容
     */
    private String content;
    /**
     * 租户编码
     */
    private String tenantCode;
}