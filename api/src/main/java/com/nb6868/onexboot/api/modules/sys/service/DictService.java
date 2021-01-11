package com.nb6868.onexboot.api.modules.sys.service;

import com.nb6868.onexboot.api.modules.sys.dto.DictDTO;
import com.nb6868.onexboot.api.modules.sys.entity.DictEntity;
import com.nb6868.onexboot.common.service.CrudService;

import java.util.List;

/**
 * 数据字典
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface DictService extends CrudService<DictEntity, DictDTO> {
    /**
     * 获得某个类型的字典列表
     *
     * @param type       类型
     * @param includePid 是否包含父级
     * @return 列表
     */
    List<DictEntity> listByType(String type, boolean includePid);

    /**
     * 通过type和value获取名称
     */
    String getNameByTypeAndValue(String type, String value);

    /**
     * 通过type和value获取名称
     */
    String getNameByTypeAndValue(String type, Integer value);
}
