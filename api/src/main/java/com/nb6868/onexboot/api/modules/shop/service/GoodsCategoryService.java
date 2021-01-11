package com.nb6868.onexboot.api.modules.shop.service;

import com.nb6868.onexboot.api.modules.shop.dto.GoodsCategoryDTO;
import com.nb6868.onexboot.api.modules.shop.dto.GoodsCategoryTreeDTO;
import com.nb6868.onexboot.api.modules.shop.entity.GoodsCategoryEntity;
import com.nb6868.onexboot.common.service.CrudService;

import java.util.List;
import java.util.Map;

/**
 * 商品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface GoodsCategoryService extends CrudService<GoodsCategoryEntity, GoodsCategoryDTO> {
    /**
     * 树状列表
     */
    List<GoodsCategoryTreeDTO> tree(Map<String, Object> params);

    /**
     * 递归上级菜单列表
     *
     * @param id 类别id
     */
    List<GoodsCategoryDTO> getParentMenuList(Long id);

}
