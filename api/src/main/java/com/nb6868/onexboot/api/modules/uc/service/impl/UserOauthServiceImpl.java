package com.nb6868.onexboot.api.modules.uc.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dao.UserOauthDao;
import com.nb6868.onexboot.api.modules.uc.dto.UserOauthDTO;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onexboot.api.modules.uc.service.UserOauthService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class UserOauthServiceImpl extends CrudServiceImpl<UserOauthDao, UserOauthEntity, UserOauthDTO> implements UserOauthService {

    @Override
    public QueryWrapper<UserOauthEntity> getWrapper(String method, Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<UserOauthEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public UserOauthEntity getByOpenid(String openid) {
        return query().eq("openid", openid).last(Const.LIMIT_ONE).one();
    }

    @Override
    public boolean unbindByUserId(Long userId) {
        return update().set("user_id", null).eq("user_id", userId).update(new UserOauthEntity());
    }

    @Override
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

    @Override
    public UserOauthEntity saveOrUpdateByWxMaUserInfo(String appId, WxMaUserInfo user) {
        UserOauthEntity userWx = getByOpenid(user.getOpenId());
        if (userWx == null) {
            // 不存在,则新增数据
            userWx = new UserOauthEntity();
            userWx.setAppid(appId);
            userWx.setType(UcConst.OauthTypeEnum.WECHAT_MA.name());
            userWx.setOpenid(user.getOpenId());
            userWx.setUnionid(user.getUnionId());
            userWx.setAvatar(user.getAvatarUrl());
            userWx.setNickname(user.getNickName());
            userWx.setExt(user.toString());
            save(userWx);
        } else {
            // 已存在,则更新数据
            userWx.setUnionid(user.getUnionId());
            userWx.setAvatar(user.getAvatarUrl());
            userWx.setNickname(user.getNickName());
            updateById(userWx);
        }
        return userWx;
    }

    @Override
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
}
