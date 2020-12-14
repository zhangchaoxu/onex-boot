package com.nb6868.onexboot.common.injector.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;

/**
 * 通过id找对应的记录数
 * int selectCountById(T entity);
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class SelectCountById extends AbstractMethod {

    /**
     * mapper 对应的方法名
     */
    private static final String MAPPER_METHOD = "selectCountById";

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sqlFmt = "SELECT COUNT(1) FROM %s WHERE %s=#{%s} %s";
        SqlSource sqlSource = new RawSqlSource(configuration, String.format(sqlFmt, tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty(), tableInfo.getLogicDeleteSql(true, true)), Object.class);
        return this.addSelectMappedStatementForOther(mapperClass, MAPPER_METHOD, sqlSource, Integer.class);
    }

}
