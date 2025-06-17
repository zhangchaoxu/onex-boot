# 角色表加入状态
ALTER TABLE `uc_role` ADD COLUMN "state" int NOT NULL DEFAULT 1 COMMENT '状态' AFTER `sort`;