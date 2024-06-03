package com.nb6868.onex.common.jpa.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.nb6868.onex.common.jpa.injector.methods.SelectCountById;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * SQL注入器
 *
 * @author Charles
 */
public class MySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
        // 增加自动填充逻辑删除
        methodList.add(new SelectCountById());
        // 内置
        // methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
        // methodList.add(new AlwaysUpdateSomeColumnById());
        return methodList;
    }

}
