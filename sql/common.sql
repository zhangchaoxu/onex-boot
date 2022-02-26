# 添加租户字段
ALTER TABLE ${tablename} ADD COLUMN tenant_code varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编码' AFTER update_time;
