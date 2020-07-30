package com.nb6868.onex.modules.sys.service.impl;

import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.ParamUtils;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.sys.dao.DictDao;
import com.nb6868.onex.modules.sys.dto.DictDTO;
import com.nb6868.onex.modules.sys.entity.DictEntity;
import com.nb6868.onex.modules.sys.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nb6868.onex.booster.pojo.Const;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 数据字典
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class DictServiceImpl extends CrudServiceImpl<DictDao, DictEntity, DictDTO> implements DictService {

    @Override
    public QueryWrapper<DictEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<DictEntity>(new QueryWrapper<>(), params)
                .eq("pid", "pid")
                .eq("type", "type")
                .like("name", "name")
                .eq("value", "value")
                .getQueryWrapper()
                .ne(ParamUtils.isEmpty(params.get("pid")), "pid", 0)
                .orderByAsc("sort", "value");
    }

    @Override
    protected void beforeSaveOrUpdateDto(DictDTO dto, int type) {
        if (type == 1) {
            // 更新下面所有的父类
            if (dto.getPid() == Const.DICT_ROOT.longValue()){
                baseMapper.update(new DictEntity(), new UpdateWrapper<DictEntity>().eq("pid", dto.getId()).set("type", dto.getType()));
            }
        }
    }

}
