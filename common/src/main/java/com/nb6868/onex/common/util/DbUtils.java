package com.nb6868.onex.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DB工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
@Slf4j
public class DbUtils {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 查询sql
     */
    public List<Map<String, Object>> executeQuerySql(String sql) {
        log.info("executeQuerySql:" + sql);
        List<Map<String, Object>> list = new ArrayList<>();
        PreparedStatement pst = null;
        SqlSession session = getSqlSession();
        ResultSet result;
        try {
            pst = session.getConnection().prepareStatement(sql);
            result = pst.executeQuery();
            ResultSetMetaData md = result.getMetaData(); //获得结果集结构信息,元数据
            int columnCount = md.getColumnCount();   //获得列数
            while (result.next()) {
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), result.getObject(i));
                }
                list.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            closeSqlSession(session);
        }
        return list;
    }

    /**
     * 获取sqlSession
     */
    public SqlSession getSqlSession() {
        return SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(), sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator());
    }

    /**
     * 关闭sqlSession
     */
    public void closeSqlSession(SqlSession session) {
        SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
    }

}
