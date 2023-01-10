package com.nb6868.onex.common.auth;

/**
 * 授权相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface AuthConst {

    // token更新
    /*String TOKEN_RENEWAL_KEY = "tokenRenewal";
    Boolean TOKEN_RENEWAL_VALUE = false;*/
    // token首次发放有效期(秒),默认7天
    String TOKEN_EXPIRE_KEY = "tokenExpire";
    Integer TOKEN_EXPIRE_VALUE = 604800;
    // token续期有效期(秒),默认7天, <=0表示不续期
    String TOKEN_RENEWAL_EXPIRE_KEY = "tokenRenewalExpire";
    Integer TOKEN_RENEWAL_EXPIRE_VALUE = 604800;
    // token密码
    String TOKEN_JWT_KEY_KEY = "tokenJwtKey";
    String TOKEN_JWT_KEY_VALUE = "onex@2021";
    // token存储方式 db cache none
    String TOKEN_STORE_TYPE_KEY = "tokenStoreType";
    String TOKEN_STORE_TYPE_VALUE = "db";
    // token限制策略 0不限制 1同type只允许一个 2同用户只允许一个
    String TOKEN_LIMIT_KEY = "tokenLimit";
    Integer TOKEN_LIMIT_VALUE = 0;

}
