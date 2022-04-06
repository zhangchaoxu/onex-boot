package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 租户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "uc_tenant_params")
@Alias("uc_tenant_params")
public class TenantParamsEntity extends BaseEntity {

    /**
     * 类型
     */
    private String type;
    /**
     * 编码
     */
    private String code;
    /**
     * 内容
     */
    private String content;
    /**
     * 租户编码
     */
    private String tenantCode;
}
