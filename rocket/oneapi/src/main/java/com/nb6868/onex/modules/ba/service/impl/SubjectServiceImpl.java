package com.nb6868.onex.modules.ba.service.impl;

import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.ba.dao.SubjectDao;
import com.nb6868.onex.modules.ba.dto.SubjectDTO;
import com.nb6868.onex.modules.ba.entity.SubjectEntity;
import com.nb6868.onex.modules.ba.service.SubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 秉奥-检测题
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class SubjectServiceImpl extends CrudServiceImpl<SubjectDao, SubjectEntity, SubjectDTO> implements SubjectService {

    @Override
    public QueryWrapper<SubjectEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<SubjectEntity>(new QueryWrapper<>(), params)
                .eq("type", "type")
                .like("question", "question")
                .getQueryWrapper();
    }

}
