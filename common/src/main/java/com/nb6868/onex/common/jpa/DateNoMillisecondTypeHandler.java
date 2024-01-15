package com.nb6868.onex.common.jpa;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.Date;

/**
 * date解析器
 * ref {DateTypeHandler}
 * mysql更新到5.6.4 之后 , 新增了factional seconds的特性 , 可以记录时间的毫秒值，超过500就会四舍五入的问题
 * 解决思路1：datetime精度改成3,记录毫秒
 * 解决思路2：插入date内容将毫秒抹零
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@MappedTypes({Date.class})
@MappedJdbcTypes(JdbcType.TIMESTAMP)
public class DateNoMillisecondTypeHandler extends BaseTypeHandler<Date> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, new Timestamp(1000 * (parameter.getTime() / 1000)));
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp sqlTimestamp = rs.getTimestamp(columnName);
        if (sqlTimestamp != null) {
            return new Date(1000 * (sqlTimestamp.getTime() / 1000));
        }
        return null;
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp sqlTimestamp = rs.getTimestamp(columnIndex);
        if (sqlTimestamp != null) {
            return new Date(1000 * (sqlTimestamp.getTime() / 1000));
        }
        return null;
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Timestamp sqlTimestamp = cs.getTimestamp(columnIndex);
        if (sqlTimestamp != null) {
            return new Date(1000 * (sqlTimestamp.getTime() / 1000));
        }
        return null;
    }
}
