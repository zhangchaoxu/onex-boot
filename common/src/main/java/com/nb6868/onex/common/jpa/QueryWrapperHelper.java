package com.nb6868.onex.common.jpa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.pojo.Const;
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
        return getPredicate(query, null);
    }

    /**
     * 封装QueryWrapper
     *
     * @param query 查询条件
     * @param from  查询来源
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public static <R, Q> QueryWrapper<R> getPredicate(Q query, String from) {
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
                if (q != null && !ArrayUtil.contains(q.exclude(), from)) {
                    Object val = entry.getValue().get(query);
                    if (q.blurryType() != Query.BlurryType.NULL) {
                        // 多字段匹配
                        List<String> blurryList = StrUtil.split(q.column(), ",", true, true);
                        if (CollUtil.isNotEmpty(blurryList) && ObjectUtil.isNotEmpty(val)) {
                            queryWrapper.and(wrapper -> {
                                for (int i = 0; i < blurryList.size(); i++) {
                                    // 只能是or和and，and默认
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
                                        case NOT_LIKE:
                                            wrapper.notLike(column, val);
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
                                        case APPLY:
                                            wrapper.apply(column, val);
                                            break;
                                        case NULL:
                                            if (val instanceof Boolean) {
                                                if ((Boolean) val) {
                                                    wrapper.isNull(column);
                                                } else {
                                                    wrapper.isNotNull(column);
                                                }
                                            }
                                            break;
                                        case EMPTY:
                                            if (val instanceof Boolean) {
                                                if ((Boolean) val) {
                                                    wrapper.and(qw -> qw.isNull(column).or().eq(column, ""));
                                                } else {
                                                    wrapper.isNotNull(column).ne(column, "");
                                                }
                                            }
                                            break;
                                    }
                                }
                            });
                        }
                    } else {
                        // 单字段匹配
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
                                queryWrapper.eq(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case EQ_STRICT:
                                if (ObjectUtil.isNotEmpty(val)) {
                                    queryWrapper.eq(ObjectUtil.isNotEmpty(val), column, val);
                                } else {
                                    queryWrapper.isNull(columnFinal);
                                }
                                break;
                            case EQ_STRICT_EMPTY:
                                if (ObjectUtil.isNotEmpty(val)) {
                                    queryWrapper.eq(ObjectUtil.isNotEmpty(val), column, val);
                                } else {
                                    queryWrapper.and(qw -> qw.isNull(columnFinal).or().eq(columnFinal, ""));
                                }
                                break;
                            case NE:
                                queryWrapper.ne(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case GE:
                                queryWrapper.ge(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case GT:
                                queryWrapper.gt(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case LE:
                                queryWrapper.le(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case LT:
                                queryWrapper.lt(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case NOT_LIKE:
                                queryWrapper.notLike(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case LIKE:
                                queryWrapper.like(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case LIKE_LEFT:
                                queryWrapper.likeLeft(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case LIKE_RIGHT:
                                queryWrapper.likeRight(ObjectUtil.isNotEmpty(val), column, val);
                                break;
                            case FIND_IN_SET:
                                queryWrapper.and(ObjectUtil.isNotEmpty(val), qw -> qw.last("find_in_set('" + val + "', " + columnFinal + ")"));
                                break;
                            case APPLY:
                                queryWrapper.apply(ObjectUtil.isNotEmpty(val), columnFinal, val);
                                break;
                            case IN:
                                // 对于IN和NOT_IN做了优化
                                // 优化点1: 支持String分割符，支持List
                                // 优化点2：对于只有1个长度的，直接用EQ或NE,多个的才IN或NOT_IN
                                if (ObjectUtil.isNotEmpty(val)) {
                                    if (val instanceof String) {
                                        // 字符串，做分割
                                        List<String> inSplitList = StrUtil.splitTrim(val.toString(), q.inSeparator());
                                        if (CollUtil.isNotEmpty(inSplitList)) {
                                            if (inSplitList.size() == 1) {
                                                queryWrapper.eq(column, inSplitList.get(0));
                                            } else {
                                                queryWrapper.in(column, inSplitList);
                                            }
                                        }
                                    } else if (val instanceof Collection) {
                                        if (val instanceof List && ((List<?>) val).size() == 1) {
                                            queryWrapper.eq(column, ((List<?>) val).get(0));
                                        } else {
                                            queryWrapper.in(column, (Collection<?>) val);
                                        }
                                    }
                                }
                                break;
                            case NOT_IN:
                                if (ObjectUtil.isNotEmpty(val)) {
                                    if (val instanceof String) {
                                        // 字符串，做分割
                                        List<String> inSplitList = StrUtil.splitTrim(val.toString(), q.inSeparator());
                                        if (CollUtil.isNotEmpty(inSplitList)) {
                                            if (inSplitList.size() == 1) {
                                                queryWrapper.ne(column, inSplitList.get(0));
                                            } else {
                                                queryWrapper.notIn(column, inSplitList);
                                            }
                                        }
                                    } else if (val instanceof Collection) {
                                        if (val instanceof List && ((List<?>) val).size() == 1) {
                                            queryWrapper.ne(column, ((List<?>) val).get(0));
                                        } else {
                                            queryWrapper.notIn(column, (Collection<?>) val);
                                        }
                                    }
                                }
                                break;
                            case NULL:
                                if (ObjectUtil.isNotNull(val) && val instanceof Boolean) {
                                    if ((Boolean) val) {
                                        queryWrapper.isNull(column);
                                    } else {
                                        queryWrapper.isNotNull(column);
                                    }
                                }
                                break;
                            case EMPTY:
                                if (ObjectUtil.isNotNull(val) && val instanceof Boolean) {
                                    if ((Boolean) val) {
                                        queryWrapper.and(qw -> qw.isNull(columnFinal).or().eq(columnFinal, ""));
                                    } else {
                                        queryWrapper.isNotNull(column).ne(column, "");
                                    }
                                }
                                break;
                            case NOT_BETWEEN:
                                if (val instanceof List) {
                                    List<?> list = (List<?>) val;
                                    if (CollUtil.emptyIfNull(list).size() == 2) {
                                        queryWrapper.notBetween(column, list.get(0), list.get(1));
                                    }
                                }
                                break;
                            case BETWEEN:
                                if (val instanceof List) {
                                    List<?> list = (List<?>) val;
                                    if (CollUtil.emptyIfNull(list).size() == 2) {
                                        queryWrapper.between(column, list.get(0), list.get(1));
                                    }
                                }
                                break;
                            case BETWEEN_TIME:
                                if (val instanceof List) {
                                    List<?> list = (List<?>) val;
                                    if (CollUtil.emptyIfNull(list).size() == 2) {
                                        queryWrapper.between(column, DateUtil.parse(list.get(0).toString()), DateUtil.parse(list.get(1).toString()));
                                    }
                                }
                                break;
                            // 分页接口中,需要过滤掉order和limit
                            case ORDER_BY:
                                if (val instanceof List) {
                                    // 注意要将参数驼峰转下划线
                                    CollUtil.emptyIfNull((List<SortItem>) val).forEach(sortItem -> queryWrapper.orderByAsc(StrUtil.isNotBlank(sortItem.getColumn()) && sortItem.getAsc(), StrUtil.toUnderlineCase(sortItem.getColumn())).orderByDesc(StrUtil.isNotBlank(sortItem.getColumn()) && !sortItem.getAsc(), StrUtil.toUnderlineCase(sortItem.getColumn())));
                                }
                                break;
                            case LIMIT:
                                queryWrapper.last(ObjectUtil.isNotNull(val), StrUtil.format(Const.LIMIT_FMT, val));
                                break;
                            default:
                                break;
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
