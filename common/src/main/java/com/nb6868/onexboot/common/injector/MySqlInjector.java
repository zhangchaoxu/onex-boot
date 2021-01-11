package com.nb6868.onexboot.common.injector;

import com.nb6868.onexboot.common.injector.methods.LogicDeleteByIdWithFill;
import com.nb6868.onexboot.common.injector.methods.LogicDeleteBatchByIdsWithFill;
import com.nb6868.onexboot.common.injector.methods.LogicDeleteByWrapperWithFill;
import com.nb6868.onexboot.common.injector.methods.SelectCountById;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SQL注入器
 *
 * @author Charles
 */
public class MySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        // 增加自动填充逻辑删除
        methodList.add(new LogicDeleteByIdWithFill());
        methodList.add(new LogicDeleteBatchByIdsWithFill());
        methodList.add(new LogicDeleteByWrapperWithFill());
        methodList.add(new SelectCountById());
        return methodList;
    }

}
