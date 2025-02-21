package com.nb6868.onex.sys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.dao.DictDao;
import com.nb6868.onex.sys.dto.DictDTO;
import com.nb6868.onex.sys.dto.DictSaveOrUpdateReq;
import com.nb6868.onex.sys.entity.DictEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.nb6868.onex.common.Const.LIMIT_ONE;

/**
 * 数据字典
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class DictService extends DtoService<DictDao, DictEntity, DictDTO> {

    /**
     * 新增或修改
     */
    @Transactional(rollbackFor = Exception.class)
    public DictEntity saveOrUpdateByReq(DictSaveOrUpdateReq req) {
        // 检查请求
        if (!Objects.equals(req.getPid(), Const.DICT_ROOT)) {
            AssertUtils.isFalse(lambdaQuery().eq(DictEntity::getId, req.getPid()).exists(), "上级不存在");
        }
        // 转换数据格式
        DictEntity entity;
        if (req.hasId()) {
            // 编辑数据
            entity = getById(req.getId());
            AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
            BeanUtil.copyProperties(req, entity);
            // 更新的时候,同时更新子类信息
            if (Objects.equals(req.getPid(), Const.DICT_ROOT) && !StrUtil.equals(entity.getType(), req.getType())) {
                lambdaUpdate().eq(DictEntity::getPid, req.getId()).set(DictEntity::getType, req.getType()).update(new DictEntity());
            }
        } else {
            // 新增数据
            // todo 只有root可以设置type
            entity = BeanUtil.copyProperties(req, DictEntity.class);
        }
        // 处理数据
        boolean ret = saveOrUpdateById(entity);
        AssertUtils.isFalse(ret, "数据更新保存失败");
        return entity;
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
