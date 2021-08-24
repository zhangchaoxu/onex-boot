package com.nb6868.onex.api.modules.uc.service;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.nb6868.onex.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onex.api.modules.uc.UcConst;
import com.nb6868.onex.api.modules.uc.dao.UserOauthDao;
import com.nb6868.onex.api.modules.uc.dto.UserOauthDTO;
import com.nb6868.onex.common.service.DtoService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.stereotype.Service;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class UserOauthService extends DtoService<UserOauthDao, UserOauthEntity, UserOauthDTO> {

    /**
     * 通过openid获取用户，openid可以保证唯一
     * @param openid
     * @return 用户
     */
    public UserOauthEntity getByOpenid(String openid) {
        return getOneByColumn("openid", openid);
    }

    /**
     * 插入或者更新微信用户
     */
    public UserOauthEntity saveOrUpdateByWxMpUser(String appId, WxMpUser user) {
        UserOauthEntity userWx = getByOpenid(user.getOpenId());
        if (userWx == null) {
            // 不存在,则新增数据
            userWx = new UserOauthEntity();
            userWx.setAppid(appId);
            userWx.setType(UcConst.OauthTypeEnum.WECHAT_MP.name());
            userWx.setOpenid(user.getOpenId());
            userWx.setUnionid(user.getUnionId());
            userWx.setAvatar(user.getHeadImgUrl());
            userWx.setNickname(user.getNickname());
            userWx.setExt(user.toString());
            save(userWx);
        } else {
            // 已存在,则更新数据
            userWx.setUnionid(user.getUnionId());
            userWx.setAvatar(user.getHeadImgUrl());
            userWx.setNickname(user.getNickname());
            userWx.setExt(user.toString());
            updateById(userWx);
        }
        return userWx;
    }

    /**
     * 插入或者更新微信小程序Session
     */
    public UserOauthEntity saveOrUpdateByWxMaJscode2SessionResult(String appId, WxMaJscode2SessionResult sessionResult) {
        UserOauthEntity userWx = getByOpenid(sessionResult.getOpenid());
        if (userWx == null) {
            // 不存在,则新增数据
            userWx = new UserOauthEntity();
            userWx.setAppid(appId);
            userWx.setType(UcConst.OauthTypeEnum.WECHAT_MA.name());
            userWx.setOpenid(sessionResult.getOpenid());
            userWx.setUnionid(sessionResult.getUnionid());
            save(userWx);
        } else {
            // 已存在,则更新数据
            userWx.setUnionid(sessionResult.getUnionid());
            updateById(userWx);
        }
        return userWx;
    }

    /**
     * 插入或者更新微信小程序用户
     */
    public UserOauthEntity saveOrUpdateByWxMaUserInfo(String appId, WxMaUserInfo user, String openId, String unionId) {
        UserOauthEntity userWx = getByOpenid(openId);
        if (userWx == null) {
            // 不存在,则新增数据
            userWx = new UserOauthEntity();
            userWx.setAppid(appId);
            userWx.setType(UcConst.OauthTypeEnum.WECHAT_MA.name());
            userWx.setOpenid(openId);
            userWx.setUnionid(unionId);
            userWx.setAvatar(user.getAvatarUrl());
            userWx.setNickname(user.getNickName());
            userWx.setExt(user.toString());
            save(userWx);
        } else {
            // 已存在,则更新数据
            userWx.setUnionid(unionId);
            userWx.setAvatar(user.getAvatarUrl());
            userWx.setNickname(user.getNickName());
            updateById(userWx);
        }
        return userWx;
    }

    /**
     * 解绑某个用户
     */
    public boolean unbindByUserId(Long userId) {
        return update().set("user_id", null).eq("user_id", userId).update(new UserOauthEntity());
    }

}
