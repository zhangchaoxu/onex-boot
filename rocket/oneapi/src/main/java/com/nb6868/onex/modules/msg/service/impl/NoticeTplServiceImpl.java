package com.nb6868.onex.modules.msg.service.impl;

import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.modules.msg.dao.NoticeTplDao;
import com.nb6868.onex.modules.msg.dto.NoticeTplDTO;
import com.nb6868.onex.modules.msg.entity.NoticeTplEntity;
import com.nb6868.onex.modules.msg.service.NoticeTplService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 通知模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class NoticeTplServiceImpl extends CrudServiceImpl<NoticeTplDao, NoticeTplEntity, NoticeTplDTO> implements NoticeTplService {

    @Override
    public QueryWrapper<NoticeTplEntity> getWrapper(String method, Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<NoticeTplEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

}
