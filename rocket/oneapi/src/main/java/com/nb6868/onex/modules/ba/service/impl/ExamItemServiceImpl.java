package com.nb6868.onex.modules.ba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.modules.ba.dao.ExamItemDao;
import com.nb6868.onex.modules.ba.dto.ExamItemDTO;
import com.nb6868.onex.modules.ba.entity.ExamItemEntity;
import com.nb6868.onex.modules.ba.service.ExamItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 秉奥-用户检测细项
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ExamItemServiceImpl extends CrudServiceImpl<ExamItemDao, ExamItemEntity, ExamItemDTO> implements ExamItemService {

    @Override
    public QueryWrapper<ExamItemEntity> getWrapper(String method, Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<ExamItemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

}
