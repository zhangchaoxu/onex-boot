package com.nb6868.onex.modules.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.uc.dao.UserOauthDao;
import com.nb6868.onex.modules.uc.dto.UserOauthDTO;
import com.nb6868.onex.modules.uc.entity.UserOauthEntity;
import com.nb6868.onex.modules.uc.service.UserOauthService;
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
    public QueryWrapper<UserOauthEntity> getWrapper(String method, Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<UserOauthEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public UserOauthEntity getByAppidAndOpenid(String type, String appid, String openid) {
        return query().eq("type", type).eq("appid", appid).eq("openid", openid).last(Const.LIMIT_ONE).one();
    }

    @Override
    public UserOauthEntity saveOrUpdateWxMpUser(String appId, WxMpUser user) {
        UserOauthEntity userWx = getByAppidAndOpenid(UcConst.OauthTypeEnum.WECHAT_MP.name(), appId, user.getOpenId());
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
            updateById(userWx);
        }
        return userWx;
    }



}
