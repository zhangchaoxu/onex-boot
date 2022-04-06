package com.nb6868.onex.uc.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
@TableName(value = "uc_user_params", autoResultMap = true)
@Alias("uc_user_params")
public class UserParamsEntity extends BaseEntity {
    /**
     * 类型
     */
    private String type;
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
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject content;
    /**
     * 租户编码
     */
    private String tenantCode;
}
