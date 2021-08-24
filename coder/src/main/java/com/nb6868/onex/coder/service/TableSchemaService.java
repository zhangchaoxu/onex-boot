package com.nb6868.onex.coder.service;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.nb6868.onex.coder.entity.DbConfigRequest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * 表结构服务
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Service
public class TableSchemaService {

    /**
     * 初始化db link
     */
    private DataSource getDataSource(DbConfigRequest request) {
        // 数据源
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(request.getDriverClassName());
        hikariConfig.setJdbcUrl(request.getDbUrl());
        hikariConfig.setUsername(request.getDbUsername());
        hikariConfig.setPassword(request.getDbPassword());
        // 设置可以获取tables remarks信息
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);
        return new HikariDataSource(hikariConfig);
    }

    private Statement getDbStatement(DataSource dataSource) throws SQLException {
        Connection dbConnection = dataSource.getConnection();
        return dbConnection.createStatement();
    }

    public List<Map<String, Object>> queryList(DbConfigRequest request) throws Exception {
        // 获得数据库连接
        Statement statement = getDbStatement(getDataSource(request));

        StringBuilder sql = new StringBuilder("select table_name, engine, table_comment, create_time from information_schema.tables where table_schema = (select database())");
        if (!ObjectUtils.isEmpty(request.getKeyword())) {
            sql.append(" and table_name like concat('%").append(request.getKeyword()).append("%')");
        }
        sql.append(" order by create_time desc");
        ResultSet resultSet = statement.executeQuery(sql.toString());
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> map = new HashMap<>();
            map.put("tableName", resultSet.getString("table_name"));
            map.put("engine", resultSet.getString("engine"));
            map.put("tableComment", resultSet.getString("table_comment"));
            map.put("createTime", resultSet.getDate("create_time"));
            resultMapList.add(map);
        }
        statement.close();
        return resultMapList;
    }

    public Map<String, String> queryTable(DbConfigRequest request) throws Exception {
        // 获得数据库连接
        Statement statement = getDbStatement(getDataSource(request));

        StringBuilder sql = new StringBuilder("select table_name, engine, table_comment, create_time from information_schema.tables where" +
                " table_schema = (select database()) and table_name = '").append(request.getTableNames()).append("'");
        ResultSet resultSet = statement.executeQuery(sql.toString());
        Map<String, String> map = null;
        while (resultSet.next()) {
            map = new HashMap<>();
            map.put("tableName", resultSet.getString("table_name"));
            map.put("engine", resultSet.getString("engine"));
            map.put("tableComment", resultSet.getString("table_comment"));
            map.put("createTime", resultSet.getString("create_time"));
        }
        statement.close();
        return map;
    }

    public List<Map<String, String>> queryColumns(DbConfigRequest request) throws Exception {
        // 获得数据库连接
        Statement statement = getDbStatement(getDataSource(request));

        StringBuilder sql = new StringBuilder("select column_name, data_type, column_comment, column_key, extra from information_schema.columns" +
                " where table_name = '").append(request.getTableNames()).append("'").append(" and table_schema = (select database()) order by ordinal_position");
        ResultSet resultSet = statement.executeQuery(sql.toString());
        List<Map<String, String>> resultMapList = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, String> map = new HashMap<>();
            map.put("columnName", resultSet.getString("column_name"));
            map.put("dataType", resultSet.getString("data_type"));
            map.put("columnComment", resultSet.getString("column_comment"));
            map.put("columnKey", resultSet.getString("column_key"));
            map.put("extra", resultSet.getString("extra"));
            resultMapList.add(map);
        }
        statement.close();
        return resultMapList;
    }

    public byte[] generateCode(DbConfigRequest request) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        // 获得表
        String[] tableNames = request.getTableNames().split(",");
        for (String tableName : tableNames) {
            // 查询表信息
            request.setTableNames(tableName);
            Map<String, String> table = queryTable(request);
            // 查询列信息
            List<Map<String, String>> columns = queryColumns(request);
            // 生成代码
            // GenUtils.generatorCode(table, columns, request.getCodeGenerateConfig(), zip);
        }
        try {
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    /**
     * 生成数据库文档
     * see {https://github.com/pingfangushi/screw}
     */
    public File generateDoc(DbConfigRequest request) throws Exception {
        DataSource dataSource = getDataSource(request);
        String timestamp = String.valueOf(System.currentTimeMillis());
        //生成配置
        EngineConfig.EngineConfigBuilder engineConfigBuilder = EngineConfig.builder()
                //生成文件路径
                .fileOutputDir("doc/" + timestamp)
                //打开目录
                .openOutputDir(false)
                .produceType(EngineTemplateType.velocity)
                .fileName(request.getDocFileName());
        EngineConfig engineConfig;
        if ("md".equalsIgnoreCase(request.getDocFileType())) {
            engineConfig = engineConfigBuilder.fileType(EngineFileType.MD).build();
        } else if ("html".equalsIgnoreCase(request.getDocFileType())) {
            engineConfig = engineConfigBuilder.fileType(EngineFileType.HTML).build();
        } else if ("word".equalsIgnoreCase(request.getDocFileType())) {
            engineConfig = engineConfigBuilder.fileType(EngineFileType.WORD).build();
        } else {
            throw new Exception("不支持的文件类型");
        }

        ProcessConfig processConfig = ProcessConfig.builder().designatedTableName(Arrays.asList(request.getTableNames().split(","))).build();
        Configuration.ConfigurationBuilder configBuilder = Configuration.builder()
                .version(request.getDocVersion())
                .description(request.getDocDescription())
                .dataSource(dataSource)
                .produceConfig(processConfig);
        new DocumentationExecute(configBuilder.engineConfig(engineConfig).build()).execute();
        return new File(engineConfig.getFileOutputDir() + File.separator + engineConfig.getFileName() + engineConfig.getFileType().getFileSuffix());
    }
}
