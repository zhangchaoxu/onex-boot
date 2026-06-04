package com.nb6868.onex.common.jpa;

import com.baomidou.mybatisplus.extension.handlers.Jackson3TypeHandler;
import com.nb6868.onex.common.util.JacksonUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import java.lang.reflect.Field;

/**
 * Jackson 实现 JSON 字段类型处理器
 * 重写以实现hutool json中JSONNull的转换
 * 具体见 [自定义类型处理器](https://baomidou.com/guides/type-handler/)
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class Jackson2TypeHandler extends Jackson3TypeHandler {

    public Jackson2TypeHandler(Class<?> type) {
        super(type);
        setObjectMapper(JacksonUtils.getMapperBuilder().build());
    }

    public Jackson2TypeHandler(Class<?> type, Field field) {
        super(type, field);
        setObjectMapper(JacksonUtils.getMapperBuilder().build());
    }

}
