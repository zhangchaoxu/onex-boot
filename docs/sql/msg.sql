SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for msg_mail_tpl
-- ----------------------------
DROP TABLE IF EXISTS `msg_mail_tpl`;
CREATE TABLE `msg_mail_tpl`
(
    `id`          bigint(20) UNSIGNED                                           NOT NULL COMMENT 'ID',
    `code`        varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '编码',
    `name`        varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '名称',
    `type`        tinyint(3)                                                    NOT NULL DEFAULT 1 COMMENT '类型 1验证码 2状态通知 3营销消息',
    `channel`     varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '渠道,短信sms/电邮email/微信公众号模板消息wx_mp_template/微信小程序订阅消息wx_ma_subscribe/站内信 notice',
    `platform`    varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL     DEFAULT NULL COMMENT '平台 如aliyun/juhe',
    `time_limit`  int(11)                                                       NOT NULL DEFAULT -1 COMMENT '限时秒数 -1表示不限',
    `title`       varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '标题',
    `content`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '内容',
    `params`       json                                                          NULL COMMENT '配置参数',
    `create_id`   bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime(0)                                                   NULL     DEFAULT NULL COMMENT '创建时间',
    `update_id`   bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime(0)                                                   NULL     DEFAULT NULL COMMENT '更新时间',
    `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '租户编码',
    `deleted`     tinyint(1) UNSIGNED                                           NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '消息-模板'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for msg_mail_log
-- ----------------------------
DROP TABLE IF EXISTS `msg_mail_log`;
CREATE TABLE `msg_mail_log`
(
    `id`             bigint(20) UNSIGNED                                           NOT NULL COMMENT 'ID',
    `tpl_code`       varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '模板编码',
    `code`           varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '验证码',
    `valid_end_time` datetime(0)                                                   NULL     DEFAULT NULL COMMENT '有效期结束',
    `mail_from`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '发送者',
    `mail_to`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '收件人',
    `mail_cc`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '抄送',
    `title`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '标题',
    `content_params` json                                                          NULL COMMENT '内容参数',
    `content`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '内容',
    `consume_state`  tinyint(3)                                                    NOT NULL DEFAULT 0 COMMENT '消费状态',
    `state`          tinyint(3)                                                    NOT NULL DEFAULT 0 COMMENT '发送状态',
    `result`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         NULL COMMENT '发送结果',
    `create_id`      bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '创建者ID',
    `create_time`    datetime(0)                                                   NULL     DEFAULT NULL COMMENT '创建时间',
    `update_id`      bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '更新者ID',
    `update_time`    datetime(0)                                                   NULL     DEFAULT NULL COMMENT '更新时间',
    `tenant_code`    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL     DEFAULT NULL COMMENT '租户编码',
    `deleted`        tinyint(1) UNSIGNED                                           NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_create_date` (`create_time`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '消息-记录'
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
