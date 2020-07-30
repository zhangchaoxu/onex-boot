package com.nb6868.onex.modules.ba.service.impl;

import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.ba.dao.TeacherDao;
import com.nb6868.onex.modules.ba.dto.TeacherDTO;
import com.nb6868.onex.modules.ba.entity.TeacherEntity;
import com.nb6868.onex.modules.ba.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 秉奥-教师
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TeacherServiceImpl extends CrudServiceImpl<TeacherDao, TeacherEntity, TeacherDTO> implements TeacherService {

    @Override
    public QueryWrapper<TeacherEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<TeacherEntity>(new QueryWrapper<>(), params)
                .eq("type", "type")
                .like("name", "name")
                .getQueryWrapper();
    }

}
