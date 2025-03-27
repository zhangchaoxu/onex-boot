# 用户表修改
ALTER TABLE `uc_user` ADD COLUMN `rel_id` bigint NULL COMMENT '关联表id' AFTER `deleted`, ADD COLUMN `rel_name` varchar(200) NULL COMMENT '关联表名称,冗余' AFTER `rel_id`;

# 用户角色表增加类型
ALTER TABLE `uc_role_user`ADD COLUMN `type` int NOT NULL DEFAULT 0 COMMENT '关系类型' AFTER `user_id`;