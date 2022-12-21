# 清空qrtz表,有外键关联,注意顺序
truncate table qrtz_cron_triggers;
truncate table qrtz_triggers;
truncate table qrtz_job_details;
