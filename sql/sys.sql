SET NAMES utf8mb4;
SET
    FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_region
-- ----------------------------
DROP TABLE IF EXISTS `sys_region`;
CREATE TABLE `sys_region`
(
    `id`            bigint(20) UNSIGNED                                           NOT NULL COMMENT '区域编号',
    `pid`           bigint(20) UNSIGNED                                           NOT NULL COMMENT '上级区域编号,0为一级',
    `name`          varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '名称',
    `code`          varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci         NULL DEFAULT NULL COMMENT '区号',
    `postcode`      varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '邮编',
    `deep`          tinyint(3)                                                    NULL DEFAULT NULL COMMENT '层级深度',
    `geo`           varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '中心点,GCJ-02.格式：\"lng lat\" or \"EMPTY\"',
    `polygon`       longtext CHARACTER SET utf8 COLLATE utf8_general_ci           NULL COMMENT '边界坐标点,GCJ-02',
    `pinyin`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拼音',
    `ext_name`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原始名称',
    `ext_id`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '原始编号',
    `pinyin_prefix` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '拼音前缀',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_id` (`id`, `name`, `pid`, `deep`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '行政区域'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_param
-- ----------------------------
DROP TABLE IF EXISTS `sys_param`;
CREATE TABLE `sys_param`
(
    `id`          bigint(20) UNSIGNED                                           NOT NULL COMMENT 'ID',
    `code`        varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT NULL COMMENT '编码',
    `type`        tinyint(3) UNSIGNED                                           NOT NULL DEFAULT 1 COMMENT '类型',
    `content`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '内容',
    `remark`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '备注',
    `create_id`   bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime(0)                                                   NULL     DEFAULT NULL COMMENT '创建时间',
    `update_id`   bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime(0)                                                   NULL     DEFAULT NULL COMMENT '更新时间',
    `tenant_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL     DEFAULT NULL COMMENT '租户编码',
    `deleted`     tinyint(1) UNSIGNED                                           NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `uk_code` (`code`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '参数'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss`
(
    `id`           bigint(20) UNSIGNED                                           NOT NULL COMMENT 'ID',
    `url`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT 'URL地址',
    `filename`     varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '文件名',
    `content_type` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT NULL COMMENT '类型',
    `size`         bigint(20)                                                    NULL     DEFAULT NULL COMMENT '尺寸',
    `create_id`    bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '创建者ID',
    `create_time`  datetime(0)                                                   NULL     DEFAULT NULL COMMENT '创建时间',
    `update_id`    bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '更新者ID',
    `update_time`  datetime(0)                                                   NULL     DEFAULT NULL COMMENT '更新时间',
    `tenant_code`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '租户编码',
    `deleted`      tinyint(1) UNSIGNED                                           NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '素材资源'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`
(
    `id`             bigint(20) UNSIGNED                                           NOT NULL COMMENT 'ID',
    `type`           varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '日志类型',
    `operation`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL     DEFAULT NULL COMMENT '用户操作',
    `uri`            varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '请求路径',
    `request_params` json                                                          NULL COMMENT '网络请求参数',
    `request_time`   int(10) UNSIGNED                                              NOT NULL DEFAULT 0 COMMENT '耗时(毫秒)',
    `state`          tinyint(3)                                                    NOT NULL COMMENT '状态',
    `content`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '内容',
    `create_name`    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL     DEFAULT NULL COMMENT '创建者名字',
    `create_id`      bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '创建者ID',
    `create_time`    datetime(0)                                                   NULL     DEFAULT NULL COMMENT '创建时间',
    `update_id`      bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '更新者ID',
    `update_time`    datetime(0)                                                   NULL     DEFAULT NULL COMMENT '更新时间',
    `tenant_code`    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL     DEFAULT NULL COMMENT '租户编码',
    `deleted`        tinyint(3)                                                    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '日志'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`
(
    `id`          bigint(20) UNSIGNED                                           NOT NULL COMMENT 'ID',
    `pid`         bigint(20) UNSIGNED                                           NOT NULL COMMENT '上级ID，一级为0',
    `type`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '类型',
    `name`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `value`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '值',
    `remark`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT NULL COMMENT '备注',
    `sort`        int(10)                                                       NULL     DEFAULT NULL COMMENT '排序',
    `create_id`   bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime(0)                                                   NULL     DEFAULT NULL COMMENT '创建时间',
    `update_id`   bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime(0)                                                   NULL     DEFAULT NULL COMMENT '更新时间',
    `tenant_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL     DEFAULT NULL COMMENT '租户编码',
    `deleted`     tinyint(1) UNSIGNED                                           NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `dict_type` (`type`, `value`) USING BTREE,
    INDEX `idx_pid` (`pid`) USING BTREE,
    INDEX `idx_sort` (`sort`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '数据字典'
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
