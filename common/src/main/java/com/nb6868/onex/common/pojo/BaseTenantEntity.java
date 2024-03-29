package com.nb6868.onex.common.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础实体类(带有租户信息)
 *
 * 对字段的修改见 FieldMetaObjectHandler
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseTenantEntity extends BaseEntity {

    /**
     * 租户编码
     */
    @TableField(fill = FieldFill.INSERT)
    private String tenantCode;

}
