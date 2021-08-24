package com.nb6868.onex.api.modules.uc.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.uc.dao.TenantDao;
import com.nb6868.onex.api.modules.uc.dto.TenantDTO;
import com.nb6868.onex.api.modules.uc.entity.TenantEntity;
import com.nb6868.onex.api.modules.uc.entity.UserEntity;
import com.nb6868.onex.common.service.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TenantService extends DtoService<TenantDao, TenantEntity, TenantDTO> {

    @Autowired
    UserService userService;

    @Override
    public QueryWrapper<TenantEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<TenantEntity>(new QueryWrapper<>(), params)
                .eq("state", "state")
                .like("name", "name")
                .getQueryWrapper();
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, TenantDTO dto, TenantEntity existedEntity, int type) {
        if (1 == type && ret && !StrUtil.equals(existedEntity.getName(), dto.getName())) {
            // 更新成功, name发生变化,更新相关业务表中的code
            userService.update().eq("tenant_id", existedEntity.getId()).set("tenant_name", dto.getName()).update(new UserEntity());
        }
    }

}
