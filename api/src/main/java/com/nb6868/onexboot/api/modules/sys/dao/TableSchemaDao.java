package com.nb6868.onexboot.api.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 表结构
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TableSchemaDao {

    /**
     * 查询表
     */
    @Select("SELECT table_name, engine, table_comment, create_time FROM information_schema.tables  WHERE table_schema = (select database()) and table_name like '%${tableName}%'")
    List<Map<String, Object>> queryTable(@Param("tableName") String tableName);

    /**
     * 查询表结构字段
     */
    @Select("SELECT column_name, data_type, column_comment, column_key, extra FROM information_schema.columns WHERE table_schema = (select database()) and table_name = ${tableName} order by ordinal_position")
    List<Map<String, Object>> queryColumns(@Param("tableName") String tableName);

    /**
     * 判断表是否存在
     */
    @Select("SELECT count(*) FROM information_schema.TABLES WHERE table_schema = (select database()) and table_name = #{tableName}")
    int isTableExisted(@Param("tableName") String tableName);

    /**
     * 删除表
     */
    @Update("DROP TABLE IF EXISTS ${tableName}")
    int dropTable(@Param("tableName") String tableName);

    /**
     * 清空表
     */
    @Update("TRUNCATE TABLE ${tableName}")
    int truncateTable(@Param("tableName") String tableName);

    /**
     * 复制表结构
     */
    @Update("CREATE TABLE ${tableNameTarget} LIKE ${tableNameSource}")
    int copyTableStructure(@Param("tableNameTarget") String tableNameTarget, @Param("tableNameSource") String tableNameSource);

    /**
     * 复制表数据
     * 表结构不一样 INSERT INTO ${tableNameTarget}(字段1,字段2,.......) SELECT 字段1,字段2,...... FROM ${tableNameSource}
     */
    @Update("INSERT INTO ${tableNameTarget} SELECT * FROM ${tableNameSource}")
    int copyTableData(@Param("tableNameTarget") String tableNameTarget, @Param("tableNameSource") String tableNameSource);

    /**
     * 修改表注释
     */
    @Update("ALTER TABLE ${tableName} COMMENT #{comment}")
    int alterTableComment(@Param("tableName") String tableName, @Param("comment") String comment);

}
