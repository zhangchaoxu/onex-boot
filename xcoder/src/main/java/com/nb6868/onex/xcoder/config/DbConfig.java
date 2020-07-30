package com.nb6868.onex.xcoder.config;

import com.nb6868.onex.xcoder.dao.*;
import com.nb6868.onex.xcoder.utils.OnexException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 数据库配置
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Configuration
public class DbConfig {

    @Value("${database: mysql}")
    private String database;

    @Autowired
    private MySqlGeneratorDao mySqlGeneratorDao;
    @Autowired
    private OracleGeneratorDao oracleGeneratorDao;
    @Autowired
    private SqlServerGeneratorDao sqlServerGeneratorDao;
    @Autowired
    private PostgreSqlGeneratorDao postgreSqlGeneratorDao;

    @Bean
    @Primary
    public GeneratorDao getGeneratorDao() {
        if ("mysql".equalsIgnoreCase(database)) {
            return mySqlGeneratorDao;
        } else if ("oracle".equalsIgnoreCase(database)) {
            return oracleGeneratorDao;
        } else if ("sqlserver".equalsIgnoreCase(database)) {
            return sqlServerGeneratorDao;
        } else if ("postgresql".equalsIgnoreCase(database)) {
            return postgreSqlGeneratorDao;
        } else {
            throw new OnexException("不支持当前数据库：" + database);
        }
    }
}
