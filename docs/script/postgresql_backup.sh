# 备份postgresql数据库
# 下面脚本是备份数据库
# 记得加到定时任务中
#!/bin/bash
PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:~/bin
export PATH
pg_dump -U 用户名 -d 数据库名 -P 密码 -F p -f /path/backup.sql
echo "----------------------------------------------------------------------------"
endDate=`date +"%Y-%m-%d %H:%M:%S"`
echo "★[$endDate] db backup Successful"
echo "----------------------------------------------------------------------------"