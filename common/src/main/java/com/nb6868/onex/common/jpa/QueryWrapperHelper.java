package com.nb6868.onex.common.jpa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        try {
            List<Field> fields = getAllFields(query.getClass(), new ArrayList<>());
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Query q = field.getAnnotation(Query.class);
                if (q != null) {
                    String propName = q.propName();
                    String blurry = q.blurry();
                    String attributeName = StrUtil.isBlank(propName) ? field.getName() : propName;
                    attributeName = StrUtil.toUnderlineCase(attributeName);
                    Object val = field.get(query);
                    if (ObjectUtil.isEmpty(val)) {
                        continue;
                    }
                    // 模糊多字段
                    List<String> blurrys = StrUtil.split(blurry, ",", true, true);
                    if (CollUtil.isNotEmpty(blurrys)) {
                        queryWrapper.and(wrapper -> {
                            for (String s : blurrys) {
                                wrapper.or().like(StrUtil.toUnderlineCase(s), val.toString());
                            }
                        });
                        continue;
                    }
                    String finalAttributeName = attributeName;
                    switch (q.type()) {
                        case EQ:
                            queryWrapper.eq(attributeName, val);
                            break;
                        case GE:
                            queryWrapper.ge(finalAttributeName, val);
                            break;
                        case GT:
                            queryWrapper.gt(finalAttributeName, val);
                            break;
                        case LE:
                            queryWrapper.le(finalAttributeName, val);
                            break;
                        case LT:
                            queryWrapper.lt(finalAttributeName, val);
                            break;
                        case LIKE:
                            queryWrapper.like(finalAttributeName, val);
                            break;
                        case LIKE_LEFT:
                            queryWrapper.likeLeft(finalAttributeName, val);
                            break;
                        case LIKE_RIGHT:
                            queryWrapper.likeRight(finalAttributeName, val);
                            break;
                        case IN:
                            if (val instanceof Collection && ObjectUtil.isNotEmpty(val)) {
                                queryWrapper.in(finalAttributeName, (Collection<?>) val);
                            }
                            break;
                        case NOT_IN:
                            if (val instanceof Collection && ObjectUtil.isNotEmpty(val)) {
                                queryWrapper.notIn(finalAttributeName, (Collection<?>) val);
                            }
                            break;
                        case NE:
                            queryWrapper.ne(finalAttributeName, val);
                            break;
                        case IS_NOT_NULL:
                            queryWrapper.isNotNull(finalAttributeName);
                            break;
                        case IS_NULL:
                            queryWrapper.isNull(finalAttributeName);
                            break;
                        case BETWEEN:
                            if (val instanceof List) {
                                List<?> list = (List<?>) val;
                                if (CollUtil.isNotEmpty(list) && list.size() == 2) {
                                    queryWrapper.between(finalAttributeName, list.get(0), list.get(1));
                                }
                            }
                            break;
                        case BETWEEN_TIME:
                            if (val instanceof List) {
                                List<?> list = (List<?>) val;
                                if (CollUtil.isNotEmpty(list) && list.size() == 2) {
                                    DateUtil.parse(list.get(0).toString());
                                    queryWrapper.between(finalAttributeName, DateUtil.parse(list.get(0).toString()), DateUtil.parse(list.get(1).toString()));
                                }
                            }
                            break;
                        case ORDER_BY:

                            break;
                        default:
                            break;
                    }
                }
                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return queryWrapper;
    }

    private static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }

}
