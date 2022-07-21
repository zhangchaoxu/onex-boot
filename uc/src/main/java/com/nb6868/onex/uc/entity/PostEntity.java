package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 岗位
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_post")
@Alias("uc_post")
public class PostEntity extends BaseEntity {
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
	private String name;
    /**
     * 备注
     */
	private String remark;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 租户编码
     */
	private String tenantCode;
}
