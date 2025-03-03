package com.nb6868.onex.common;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface Const {

    /**
     * 未登录提醒
     */
    String MSG_LOGIN_REQUIRED = "请先登录...";
    /**
     * 登录信息过期提醒
     */
    String MSG_LOGIN_EXPIRED = "请重新登录...";

    String AES_KEY = "1234567890adbcde";

    /**
     * 数据字典根节点标识
     */
    Long DICT_ROOT = 0L;
    /**
     * Excel导入记录数限制
     */
    Long EXCEL_IMPORT_LIMIT = 1000L;
    /**
     *  升序
     */
    String ASC = "asc";
    /**
     * 降序
     */
    String DESC = "desc";
    /**
     * 创建时间字段名
     */
    String CREATE_DATE = "create_time";
    /**
     * 限定一条记录
     */
    String LIMIT_ONE = "LIMIT 1";

    /**
     * 数据权限过滤
     */
    String SQL_FILTER = "sqlFilter";

    /**
     * 数据权限过滤,自己创建的
     */
    String SQL_FILTER_MY = "sqlFilterMy";
    /**
     * JSON SQL key
     */
    String SQL_JSON_KEY = "{}->'$.{}'";

    /**
     * 当前页码
     */
    String PAGE = "page";
    /**
     * 每页显示记录数
     */
    String LIMIT = "limit";
    /**
     * 限制条数
     */
    String LIMIT_FMT = "limit {}";
    /**
     * 限制条数,范围
     */
    String LIMIT_RANGE_FMT = "limit {},{}";
    /**
     * 排序字段
     */
    String ORDER_FIELD = "orderField";
    /**
     * 排序方式
     */
    String ORDER = "order";
    /**
     * 消息推送配置KEY
     */
    String PUSH_CONFIG_KEY = "PUSH_CONFIG_KEY";
    /**
     * 默认页码
     */
    Long DEFAULT_PAGE_NO = 1L;
    /**
     * 默认页数
     */
    Long DEFAULT_PAGE_SIZE = 10L;
    /**
     * 请求contentDisposition
     */
    String CONTENT_DISPOSITION_ATTACHMENT = "attachment;filename={}";
    String CONTENT_DISPOSITION_INLINE = "inline;filename={}";

    /**
     * 结果枚举
     */
    @Getter
    @AllArgsConstructor
    enum ResultEnum {

        /**
         * 详见name
         */
        SUCCESS(1, "成功"),
        FAIL(0, "失败");

        private int code;
        private String title;

        public static ResultEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static ResultEnum getByBoolean(boolean ret) {
            return ret ? SUCCESS : FAIL;
        }

        public static String getTitleByCode(Integer codeValue) {
            ResultEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }

    }

    /**
     * 二元布尔枚举
     */
    @Getter
    @AllArgsConstructor
    enum BooleanEnum {

        /**
         * 详见name
         */
        TRUE(1, "是"),
        FALSE(0, "否");

        private int code;
        private String title;

        public static BooleanEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static BooleanEnum getByBoolean(boolean ret) {
            return ret ? TRUE : FALSE;
        }

        public static String getTitleByCode(Integer codeValue) {
            BooleanEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }

    }

}
