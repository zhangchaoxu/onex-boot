package com.nb6868.onexboot.api.modules.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.sys.dao.DictDao;
import com.nb6868.onexboot.api.modules.sys.dto.DictDTO;
import com.nb6868.onexboot.api.modules.sys.entity.DictEntity;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.ParamUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.nb6868.onexboot.common.pojo.Const.LIMIT_ONE;

/**
 * 数据字典
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class DictService extends DtoService<DictDao, DictEntity, DictDTO> {

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
        if (type == 1 && dto.getPid() == Const.DICT_ROOT.longValue()) {
            // 更新下面所有的父类
            update().eq("pid", dto.getId()).set("type", dto.getType()).update(new DictEntity());
        }
    }

    /**
     * 获得某个类型的字典列表
     *
     * @param type       类型
     * @param includePid 是否包含父级
     * @return 列表
     */
    public List<DictEntity> listByType(String type, boolean includePid) {
        return query().eq("type", type).ne(!includePid, "pid", 0).orderByAsc("sort").list();
    }

    /**
     * 通过type和value获取名称
     */
    public String getNameByTypeAndValue(String type, Integer value) {
        return value != null ? getNameByTypeAndValue(type, String.valueOf(value)) : null;
    }


    /**
     * 通过type和value获取名称
     */
    public String getNameByTypeAndValue(String type, String value) {
        return query().select("name").eq("type", type).eq("value", value).last(LIMIT_ONE).oneOpt().map(DictEntity::getName).orElse(null);
    }

}
