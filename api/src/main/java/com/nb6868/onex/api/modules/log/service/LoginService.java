package com.nb6868.onex.api.modules.log.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.log.dao.LoginDao;
import com.nb6868.onex.api.modules.log.dto.LoginDTO;
import com.nb6868.onex.api.modules.log.entity.LoginEntity;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 登录日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class LoginService extends DtoService<LoginDao, LoginEntity, LoginDTO> {

    @Override
    public QueryWrapper<LoginEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<LoginEntity>(new QueryWrapper<>(), params)
                // 状态
                .eq("state", "state")
                // 用户
                .like("createName", "create_name")
                // 操作IP
                .like("ip", "ip")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .getQueryWrapper();
    }

}
