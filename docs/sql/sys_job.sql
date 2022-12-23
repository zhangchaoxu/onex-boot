SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`
(
    `id`          bigint(20) NOT NULL COMMENT 'ID',
    `code`        varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编码',
    `state`       tinyint(3) NULL DEFAULT 0 COMMENT '状态',
    `cron`        varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT 'cron表达式',
    `log_type`    varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '日志类型',
    `params`      json NULL COMMENT '参数',
    `remark`      varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    `create_id`   bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_id`   bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `tenant_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户编码',
    `deleted`     tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_normal`(`code`, `tenant_code`, `deleted`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统-定时任务' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`
(
    `id`            bigint(20) UNSIGNED NOT NULL COMMENT 'ID',
    `job_id`        bigint(20) UNSIGNED NOT NULL COMMENT '任务ID',
    `job_code`      varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务编码',
    `params`        json NULL COMMENT '执行参数',
    `state`         tinyint(3) NOT NULL COMMENT '状态',
    `result`        text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '结果',
    `error`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',
    `time_interval` int(11) NOT NULL COMMENT '耗时',
    `create_id`     bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '创建者ID',
    `create_time`   datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_id`     bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '更新者ID',
    `update_time`   datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `tenant_code`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编码',
    `deleted`       tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX           `idx_task_id`(`job_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统-定时任务日志' ROW_FORMAT = DYNAMIC;

SET
FOREIGN_KEY_CHECKS = 1;
