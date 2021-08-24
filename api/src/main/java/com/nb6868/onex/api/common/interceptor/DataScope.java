package com.nb6868.onex.api.common.interceptor;

/**
 * 数据范围
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DataScope {

    private String sqlFilter;

    public DataScope(String sqlFilter) {
        this.sqlFilter = sqlFilter;
    }

    public String getSqlFilter() {
        return sqlFilter;
    }

    public void setSqlFilter(String sqlFilter) {
        this.sqlFilter = sqlFilter;
    }

    @Override
    public String toString() {
        return this.sqlFilter;
    }

}
