package com.nb6868.onex.modules.ba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.ba.dao.ExamUserDao;
import com.nb6868.onex.modules.ba.dto.ExamUserDTO;
import com.nb6868.onex.modules.ba.entity.ExamUserEntity;
import com.nb6868.onex.modules.ba.service.ExamUserService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 秉奥-用户检测
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ExamUserServiceImpl extends CrudServiceImpl<ExamUserDao, ExamUserEntity, ExamUserDTO> implements ExamUserService {

    @Override
    public QueryWrapper<ExamUserEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<ExamUserEntity>(new QueryWrapper<>(), params)
                .eq("type", "type")
                .eq("userId", "user_id")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .getQueryWrapper();
    }

}
