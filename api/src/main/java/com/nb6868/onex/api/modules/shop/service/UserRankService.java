package com.nb6868.onex.api.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onex.api.modules.shop.entity.UserRankEntity;
import com.nb6868.onex.api.modules.uc.service.UserService;
import com.nb6868.onex.api.modules.shop.dao.UserRankDao;
import com.nb6868.onex.api.modules.shop.dto.UserRankDTO;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.service.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

/**
 * 用户等级
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class UserRankService extends DtoService<UserRankDao, UserRankEntity, UserRankDTO> {

    @Autowired
    private UserService userService;

    @Override
    public QueryWrapper<UserRankEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<UserRankEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    protected void beforeSaveOrUpdateDto(UserRankDTO dto, int type) {
        if (dto.getDefaultItem() == Const.BooleanEnum.TRUE.value()) {
            // 默认
            AssertUtils.isTrue(hasDuplicated(dto.getId(), "default_item", 1), "只能存在一个默认等级");
        } else {
            // 非默认
            AssertUtils.isFalse(hasDuplicated(dto.getId(), "default_item", 1), "必须有一个默认等级");
        }
    }

    @Override
    public boolean logicDeleteById(Serializable id) {
        AssertUtils.isTrue(SqlHelper.retBool(userService.query().eq("shop_user_rank_id", id).count()), "级别下存在会员,不允许删除");
        return super.logicDeleteById(id);
    }

}
