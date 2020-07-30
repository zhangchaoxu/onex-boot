package com.nb6868.onex.modules.sys.entity;

import com.nb6868.onex.booster.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数管理
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_param")
public class ParamEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 参数编码
     */
    private String code;
    /**
     * 类型   0：系统参数   1：非系统参数
     */
    private Integer type;
    /**
     * 参数值
     */
    private String content;
    /**
     * 备注
     */
    private String remark;

}
