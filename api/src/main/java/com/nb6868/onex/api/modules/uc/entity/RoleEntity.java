package com.nb6868.onex.api.modules.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_role")
public class RoleEntity extends BaseEntity {

    /**
     * 名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;

}
