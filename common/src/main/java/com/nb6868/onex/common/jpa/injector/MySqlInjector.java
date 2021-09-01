package com.nb6868.onex.common.jpa.injector;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.nb6868.onex.common.jpa.injector.methods.LogicDeleteBatchByIdsWithFill;
import com.nb6868.onex.common.jpa.injector.methods.LogicDeleteByIdWithFill;
import com.nb6868.onex.common.jpa.injector.methods.LogicDeleteByWrapperWithFill;
import com.nb6868.onex.common.jpa.injector.methods.SelectCountById;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

import java.util.List;

/**
 * SQL注入器
 *
 * @author Charles
 */
public class MySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        // 增加自动填充逻辑删除
        methodList.add(new LogicDeleteByIdWithFill());
        methodList.add(new LogicDeleteBatchByIdsWithFill());
        methodList.add(new LogicDeleteByWrapperWithFill());
        methodList.add(new SelectCountById());
        return methodList;
    }

}
