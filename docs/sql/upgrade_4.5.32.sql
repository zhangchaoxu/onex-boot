# 角色表加入状态
ALTER TABLE `uc_role` ADD COLUMN `state` tinyint NOT NULL DEFAULT 1 COMMENT '状态' AFTER `sort`;
# 部门加入状态
ALTER TABLE `uc_dept` ADD COLUMN `state` tinyint NOT NULL DEFAULT 1 COMMENT '状态' AFTER `sort`;