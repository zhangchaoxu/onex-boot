# 关联表增加右表名称
ALTER TABLE `sys_relation` ADD COLUMN `right_name` varchar(500) NULL COMMENT '右表名称' AFTER `right_id`;