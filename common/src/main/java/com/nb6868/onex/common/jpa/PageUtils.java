package com.nb6868.onex.common.jpa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.PageForm;
import com.nb6868.onex.common.pojo.SortItem;

import java.util.Map;

/**
 * 分页工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class PageUtils {

    /**
     * 获取分页对象
     *
     * @param pageForm 分页查询表单
     */
    public static <T> IPage<T> getPageByForm(PageForm pageForm) {
        // 分页对象 参数,当前页和每页数
        Page<T> page = Page.of(Opt.ofNullable(pageForm.getPageNo()).orElse(Const.DEFAULT_PAGE_NO), Opt.ofNullable(pageForm.getPageSize()).orElse(Const.DEFAULT_PAGE_SIZE));
        // 排序
        CollUtil.emptyIfNull(pageForm.getSortItems()).forEach(sortItem -> {
            OrderItem orderItem = sortItemToOrderItem(sortItem);
            if (null != orderItem) {
                page.addOrder(orderItem);
            }
        });
        return page;
    }

    /**
     * 获取分页对象
     *
     * @param params            分页查询参数
     * @param defaultOrderField 默认排序字段
     * @param isAsc             排序方式
     */
    public static <T> IPage<T> getPageByMap(Map<String, Object> params, String defaultOrderField, boolean isAsc) {
        // 分页对象 参数,当前页和每页数
        Page<T> page = new Page<>(MapUtil.getLong(params, Const.PAGE, 1L), MapUtil.getLong(params, Const.LIMIT, 10L));

        // 分页参数?
        // params.put(Constant.PAGE, page);

        // 排序字段
        String orderField = (String) params.get(Const.ORDER_FIELD);
        String order = (String) params.get(Const.ORDER);

        // 前端字段排序
        if (StringUtils.isNotBlank(orderField) && StringUtils.isNotBlank(order)) {
            OrderItem.asc(orderField);
            return page.addOrder(Const.ASC.equalsIgnoreCase(order) ? OrderItem.asc(orderField) : OrderItem.desc(orderField));
        }

        // 没有排序字段，则不排序
        if (StringUtils.isBlank(defaultOrderField)) {
            return page;
        }

        // 默认排序
        page.addOrder(isAsc ? OrderItem.asc(orderField) : OrderItem.desc(orderField));

        return page;
    }

    /**
     * 排序转换
     */
    public static OrderItem sortItemToOrderItem(SortItem sortItem) {
        if (StrUtil.isBlank(sortItem.getColumn())) {
            return null;
        }
        return sortItem.getAsc() ? OrderItem.asc(StrUtil.toUnderlineCase(sortItem.getColumn())) : OrderItem.desc(StrUtil.toUnderlineCase(sortItem.getColumn()));
    }

}
