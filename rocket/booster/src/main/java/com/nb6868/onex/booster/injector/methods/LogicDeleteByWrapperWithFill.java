package com.nb6868.onex.booster.injector.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * 根据 Wrapper 逻辑删除数据,并带字段填充功能
 * <p>注意入参有 entity !!! ,如果字段没有自动填充,就只是单纯的逻辑删除</p>
 * <p>
 * 自己的通用 mapper 如下使用:
 * int deleteByWrapperWithFill(T entity, Wrapper wrapper);
 *
 * @author Charles
 */
public class LogicDeleteByWrapperWithFill extends AbstractMethod {

    /**
     * mapper 对应的方法名
     */
    private static final String MAPPER_METHOD = "deleteByWrapperWithFill";

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE;
        String sql;
        if (tableInfo.isWithLogicDelete()) {
            // 包含->逻辑删除
            // 自动填充字段
            List<TableFieldInfo> fieldInfos = tableInfo.getFieldList().stream().filter(TableFieldInfo::isWithUpdateFill).collect(toList());
            // 自动填充sql
            String sqlSet;
            if (CollectionUtils.isNotEmpty(fieldInfos)) {
                // 包含->自动填充字段
                sqlSet = "SET " + fieldInfos.stream().map(i -> i.getSqlSet(ENTITY_DOT)).collect(joining(EMPTY)) + tableInfo.getLogicDeleteSql(false, false);
            } else {
                // 不包含->自动填充字段
                sqlSet = sqlLogicSet(tableInfo);
            }
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlSet, sqlWhereEntityWrapper(true, tableInfo), sqlComment());
        } else {
            // 不包含->逻辑删除
            sqlMethod = SqlMethod.DELETE;
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlWhereEntityWrapper(true, tableInfo), sqlComment());
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addDeleteMappedStatement(mapperClass, MAPPER_METHOD, sqlSource);
    }

}
