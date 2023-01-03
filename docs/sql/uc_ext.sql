SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for uc_menu
-- ----------------------------
DROP TABLE IF EXISTS `uc_menu`;
CREATE TABLE `uc_menu`
(
    `id`            bigint UNSIGNED NOT NULL COMMENT 'ID',
    `pid`           bigint UNSIGNED NOT NULL COMMENT '上级ID，一级菜单为0',
    `type`          tinyint UNSIGNED NULL DEFAULT NULL COMMENT '类型 0菜单/页面,1按钮/接口',
    `name`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
    `show_menu`     tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否显示',
    `url`           varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单或页面URL',
    `url_new_blank` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '菜单新页面打开',
    `permissions`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：sys:user:list,sys:user:save)',
    `icon`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
    `sort`          int UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序',
    `create_id`     bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建者ID',
    `create_time`   datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_id`     bigint UNSIGNED NULL DEFAULT NULL COMMENT '更新者ID',
    `update_time`   datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `tenant_code`   varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户编码',
    `deleted`       tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX           `idx_normal`(`pid`, `tenant_code`, `deleted`, `type`, `sort`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户中心-菜单(权限)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for uc_menu_scope
-- ----------------------------
DROP TABLE IF EXISTS `uc_menu_scope`;
CREATE TABLE `uc_menu_scope`
(
    `id`               bigint UNSIGNED NOT NULL COMMENT 'ID',
    `type`             tinyint NOT NULL DEFAULT 1 COMMENT '授权类型，1角色授权 2用户授权',
    `user_id`          bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
    `role_id`          bigint UNSIGNED NULL DEFAULT NULL COMMENT '角色ID',
    `menu_id`          bigint UNSIGNED NOT NULL COMMENT '菜单ID',
    `menu_permissions` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单授权',
    `create_id`        bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建者ID',
    `create_time`      datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_id`        bigint UNSIGNED NULL DEFAULT NULL COMMENT '更新者ID',
    `update_time`      datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`          tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX              `idx_menu`(`menu_id`, `type`, `user_id`, `role_id`, `deleted`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户中心-角色(用户)与菜单权限关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for uc_role
-- ----------------------------
DROP TABLE IF EXISTS `uc_role`;
CREATE TABLE `uc_role`
(
    `id`          bigint UNSIGNED NOT NULL COMMENT 'id',
    `name`        varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
    `remark`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    `sort`        int                                                     NOT NULL DEFAULT 0 COMMENT '排序',
    `create_id`   bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_id`   bigint UNSIGNED NULL DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `tenant_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户编码',
    `deleted`     tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_normal`(`tenant_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户中心-角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for uc_role_user
-- ----------------------------
DROP TABLE IF EXISTS `uc_role_user`;
CREATE TABLE `uc_role_user`
(
    `id`          bigint UNSIGNED NOT NULL COMMENT 'ID',
    `role_id`     bigint UNSIGNED NOT NULL COMMENT '角色ID',
    `user_id`     bigint UNSIGNED NOT NULL COMMENT '用户ID',
    `create_id`   bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建者id',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_id`   bigint UNSIGNED NULL DEFAULT NULL COMMENT '更新者id',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_role_user`(`role_id`, `user_id`, `deleted`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-用户关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for uc_token
-- ----------------------------
DROP TABLE IF EXISTS `uc_token`;
CREATE TABLE `uc_token`
(
    `id`          bigint UNSIGNED NOT NULL COMMENT 'ID',
    `user_id`     bigint UNSIGNED NOT NULL COMMENT '用户ID',
    `token`       varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户token',
    `type`        varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录类型',
    `expire_time` datetime(0) NOT NULL COMMENT '失效时间',
    `create_id`   bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_id`   bigint UNSIGNED NULL DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `tenant_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户编码',
    `deleted`     tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`, `token`) USING BTREE,
    UNIQUE INDEX `uk_token`(`token`, `deleted`) USING BTREE,
    INDEX         `pk_user`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户中心-Token' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for uc_user
-- ----------------------------
DROP TABLE IF EXISTS `uc_user`;
CREATE TABLE `uc_user`
(
    `id`           bigint UNSIGNED NOT NULL COMMENT 'ID',
    `type`         tinyint UNSIGNED NOT NULL COMMENT '类型',
    `code`         varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编号',
    `area_code`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区域编码',
    `dept_code`    varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门编码',
    `post_code`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位编码',
    `level`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '等级',
    `username`     varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
    `password`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
    `password_raw` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码RAW',
    `real_name`    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
    `invite_code`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邀请码',
    `nickname`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
    `mobile`       varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
    `email`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
    `id_no`        varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号',
    `avatar`       varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
    `remark`       varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    `gender`       tinyint UNSIGNED NULL DEFAULT NULL COMMENT '性别',
    `balance`      decimal(10, 2)                                         NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    `points`       decimal(10, 2)                                         NOT NULL DEFAULT 0.00 COMMENT '积分',
    `income`       decimal(10, 2)                                         NOT NULL DEFAULT 0.00 COMMENT '收入余额',
    `state`        tinyint                                                NOT NULL COMMENT '状态',
    `ext_info`     json NULL COMMENT '额外信息',
    `create_id`    bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建者ID',
    `create_time`  datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_id`    bigint UNSIGNED NULL DEFAULT NULL COMMENT '更新者ID',
    `update_time`  datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `tenant_code`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编码',
    `deleted`      tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `idx_mobile`(`mobile`) USING BTREE,
    INDEX          `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户中心-用户' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
