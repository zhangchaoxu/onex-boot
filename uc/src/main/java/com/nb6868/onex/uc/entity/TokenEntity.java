package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * 用户Token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_token")
@Alias("uc_token")
public class TokenEntity extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户token
     */
    private String token;
    /**
     * 登录类型
     */
    private String type;
    /**
     * 失效时间
     */
    private Date expireTime;
    /**
     * 租户编码
     */
    private String tenantCode;
}
