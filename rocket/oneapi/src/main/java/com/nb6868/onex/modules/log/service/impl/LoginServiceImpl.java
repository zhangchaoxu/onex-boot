package com.nb6868.onex.modules.log.service.impl;

import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.log.dao.LoginDao;
import com.nb6868.onex.modules.log.dto.LoginDTO;
import com.nb6868.onex.modules.log.entity.LoginEntity;
import com.nb6868.onex.modules.log.service.LoginService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 登录日志
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Service
public class LoginServiceImpl extends CrudServiceImpl<LoginDao, LoginEntity, LoginDTO> implements LoginService {

    @Override
    public QueryWrapper<LoginEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<LoginEntity>(new QueryWrapper<>(), params)
                // 状态
                .eq("status", "status")
                // 用户
                .like("createName", "create_name")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .getQueryWrapper();
    }

}
