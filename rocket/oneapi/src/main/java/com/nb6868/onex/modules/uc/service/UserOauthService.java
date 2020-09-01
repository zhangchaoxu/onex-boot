package com.nb6868.onex.modules.uc.service;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.uc.dto.UserOauthDTO;
import com.nb6868.onex.modules.uc.entity.UserOauthEntity;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface UserOauthService extends CrudService<UserOauthEntity, UserOauthDTO> {

    /**
     * 通过appid和openid获取用户
     * @param appid
     * @param openid
     * @return
     */
    UserOauthEntity getByAppidAndOpenid(String appid, String openid);

    /**
     * 插入或者更新微信用户
     */
    UserOauthEntity saveOrUpdateWxMpUser(String appId, WxMpUser user);

    /**
     * 插入或者更新微信小程序Session
     */
    UserOauthEntity saveOrUpdateByWxMaJscode2SessionResult(String appId, WxMaJscode2SessionResult sessionResult);

    /**
     * 解绑某个用户
     */
    boolean unbindByUserId(Long userId);

}
