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
@TableName(value = "uc_params", autoResultMap = true)
@Alias("uc_params")
public class ParamsEntity extends BaseEntity {
    /**
     * 类型
     */
    private Integer type;
    /**
     * 数据开放范围
     */
    private String scope;
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
    /**
     * 备注
     */
    private String remark;
}
