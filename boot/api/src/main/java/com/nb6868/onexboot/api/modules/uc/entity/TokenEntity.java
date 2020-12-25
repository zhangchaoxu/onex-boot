package com.nb6868.onexboot.api.modules.uc.entity;

import com.nb6868.onexboot.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_token")
public class TokenEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户token
     */
    private String token;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 类型
     */
    private Integer type;

}
