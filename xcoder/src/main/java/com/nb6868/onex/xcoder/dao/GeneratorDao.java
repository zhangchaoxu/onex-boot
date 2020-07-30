package com.nb6868.onex.xcoder.dao;

import java.util.List;
import java.util.Map;

/**
 * 数据库接口
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public interface GeneratorDao {
    List<Map<String, Object>> queryList(Map<String, Object> map);

    Map<String, String> queryTable(String tableName);

    List<Map<String, String>> queryColumns(String tableName);
}
