# 用户表修改
ALTER TABLE `uc_user` ADD COLUMN `rel_id` bigint NULL COMMENT '关联表id' AFTER `deleted`, ADD COLUMN `rel_name` varchar(200) NULL COMMENT '关联表名称,冗余' AFTER `rel_id`;

# 用户角色表增加类型
ALTER TABLE `uc_role_user`ADD COLUMN `type` int NOT NULL DEFAULT 0 COMMENT '关系类型' AFTER `user_id`;

# 日志表加入请求相关字段
ALTER TABLE `sys_log` ADD COLUMN `request_ip` varchar(50) NULL COMMENT '请求IP' AFTER `request_time`,ADD COLUMN `request_ip_region` varchar(100) NULL COMMENT '请求IP所在区域' AFTER `request_ip`,ADD COLUMN `request_ua` varchar(200) NULL COMMENT '请求UA' AFTER `request_ip_region`,ADD COLUMN `request_body` json NULL COMMENT '请求内容' AFTER `request_ua`;

# 定时任务日志表加入创建人
ALTER TABLE `sys_job_log` ADD COLUMN `create_name` varchar(100) NULL COMMENT '创建者名字' AFTER `create_id`;

# 部门表加入第三方关联信息和PID
ALTER TABLE `uc_dept` ADD COLUMN `pid` bigint NOT NULL DEFAULT 0 COMMENT 'PID' AFTER `id`, ADD COLUMN `oauth_deptid` varchar(200) NULL COMMENT '第三方部门id' AFTER `sort`, ADD COLUMN `oauth_info` json NULL COMMENT '第三方部门信息' AFTER `oauth_deptid`;

# 新建部门用户关系表
CREATE TABLE `uc_dept_user`  (
                                 `id` bigint UNSIGNED NOT NULL COMMENT 'ID',
                                 `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门id',
                                 `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                 `type` int NOT NULL DEFAULT 0 COMMENT '关系类型',
                                 `create_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建者id',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '更新者id',
                                 `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                 `deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_role_user`(`dept_id` ASC, `user_id` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户中心-部门用户关系' ROW_FORMAT = Dynamic;

# 菜单表加入component和meta
ALTER TABLE `uc_menu` ADD COLUMN `component` varchar(200) NULL COMMENT '组件' AFTER `show_menu`, ADD COLUMN `meta` json NULL COMMENT '元数据' AFTER `component`;