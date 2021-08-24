package com.nb6868.onex.api.modules.sys.entity;

import com.nb6868.onex.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数管理
 *
 * @author Charles zhangchaoxu@gmail.com
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
     * 类型
     */
    private Integer type;
    /**
     * 内容
     */
    private String content;
    /**
     * 备注
     */
    private String remark;

}
