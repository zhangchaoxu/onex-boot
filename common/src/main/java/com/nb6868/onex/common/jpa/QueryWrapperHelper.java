package com.nb6868.onex.common.jpa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.pojo.SortItem;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 查询条件组装工具
 *
 * @author Zheng Jie
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class QueryWrapperHelper {

    /**
     * 封装QueryWrapper
     */
    public static <R, Q> QueryWrapper<R> getPredicate(Q query) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();
        if (query == null) {
            return queryWrapper;
        }
        Map<String, Field> fields = ReflectUtil.getFieldMap(query.getClass());
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            try {
                boolean accessible = entry.getValue().isAccessible();
                entry.getValue().setAccessible(true);
                Query q = entry.getValue().getAnnotation(Query.class);
                if (q != null) {

                    Object val = entry.getValue().get(query);
                    if (ObjectUtil.isNotEmpty(val)) {
                        if (q.blurryType() != Query.BlurryType.NULL) {
                            // 多字段
                            List<String> blurryList = StrUtil.split(q.column(), ",", true, true);
                            queryWrapper.and(wrapper -> {
                                for (int i = 0; i < blurryList.size(); i++) {
                                    wrapper.or(q.blurryType() == Query.BlurryType.OR && i != 0);
                                    final String column = q.underlineCase() ? StrUtil.toUnderlineCase(blurryList.get(i)) : blurryList.get(i);
                                    switch (q.type()) {
                                        case EQ:
                                            wrapper.eq(column, val);
                                            break;
                                        case NE:
                                            wrapper.ne(column, val);
                                            break;
                                        case GE:
                                            wrapper.ge(column, val);
                                            break;
                                        case GT:
                                            wrapper.gt(column, val);
                                            break;
                                        case LE:
                                            wrapper.le(column, val);
                                            break;
                                        case LT:
                                            wrapper.lt(column, val);
                                            break;
                                        case LIKE:
                                            wrapper.like(column, val);
                                            break;
                                        case LIKE_LEFT:
                                            wrapper.likeLeft(column, val);
                                            break;
                                        case LIKE_RIGHT:
                                            wrapper.likeRight(column, val);
                                            break;
                                        case IS_NOT_NULL:
                                            wrapper.isNotNull(column);
                                            break;
                                        case IS_NULL:
                                            wrapper.isNull(column);
                                            break;
                                        case IS_NOT_EMPTY:
                                            wrapper.and(qw -> qw.isNotNull(column).ne(column, ""));
                                            break;
                                        case IS_EMPTY:
                                            wrapper.and(qw -> qw.isNull(column).or().eq(column, ""));
                                            break;
                                    }
                                }
                            });
                        } else {
                            String column = q.column();
                            if (StrUtil.isBlank(column)) {
                                column = entry.getValue().getName();
                            }
                            if (q.underlineCase()) {
                                column = StrUtil.toUnderlineCase(column);
                            }
                            final String columnFinal = column;
                            switch (q.type()) {
                                case EQ:
                                    queryWrapper.eq(column, val);
                                    break;
                                case NE:
                                    queryWrapper.ne(column, val);
                                    break;
                                case GE:
                                    queryWrapper.ge(column, val);
                                    break;
                                case GT:
                                    queryWrapper.gt(column, val);
                                    break;
                                case LE:
                                    queryWrapper.le(column, val);
                                    break;
                                case LT:
                                    queryWrapper.lt(column, val);
                                    break;
                                case LIKE:
                                    queryWrapper.like(column, val);
                                    break;
                                case LIKE_LEFT:
                                    queryWrapper.likeLeft(column, val);
                                    break;
                                case LIKE_RIGHT:
                                    queryWrapper.likeRight(column, val);
                                    break;
                                case IN:
                                    if (val instanceof Collection && ObjectUtil.isNotEmpty(val)) {
                                        queryWrapper.in(column, (Collection<?>) val);
                                    }
                                    break;
                                case NOT_IN:
                                    if (val instanceof Collection && ObjectUtil.isNotEmpty(val)) {
                                        queryWrapper.notIn(column, (Collection<?>) val);
                                    }
                                    break;
                                case IS_NOT_NULL:
                                    queryWrapper.isNotNull(column);
                                    break;
                                case IS_NULL:
                                    queryWrapper.isNull(column);
                                    break;
                                case IS_NOT_EMPTY:
                                    queryWrapper.and(qw -> qw.isNotNull(columnFinal).ne(columnFinal, ""));
                                    break;
                                case IS_EMPTY:
                                    queryWrapper.and(qw -> qw.isNull(columnFinal).or().eq(columnFinal, ""));
                                    break;
                                case BETWEEN:
                                    if (val instanceof List) {
                                        List<?> list = (List<?>) val;
                                        if (CollUtil.isNotEmpty(list) && list.size() == 2) {
                                            queryWrapper.between(column, list.get(0), list.get(1));
                                        }
                                    }
                                    break;
                                case BETWEEN_TIME:
                                    if (val instanceof List) {
                                        List<?> list = (List<?>) val;
                                        if (CollUtil.isNotEmpty(list) && list.size() == 2) {
                                            DateUtil.parse(list.get(0).toString());
                                            queryWrapper.between(column, DateUtil.parse(list.get(0).toString()), DateUtil.parse(list.get(1).toString()));
                                        }
                                    }
                                    break;
                                case ORDER_BY:
                                    if (val instanceof List) {
                                        List<SortItem> list = (List<SortItem>) val;
                                        if (CollUtil.isNotEmpty(list)) {
                                            list.forEach(sortItem -> queryWrapper.orderByAsc(sortItem.getAsc(), sortItem.getColumn()).orderByDesc(!sortItem.getAsc(), sortItem.getColumn()));
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                entry.getValue().setAccessible(accessible);
            } catch (Exception e) {
                log.error("QueryWrapperHelper", e);
            }
        }
        return queryWrapper;
    }

}
