# 还原postgresql数据库
# 下面脚本是备份数据库
# 记得加到定时任务中
#!/bin/bash
PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:~/bin
export PATH
psql -U 用户名 -d 数据库名 -f /path/backup.sql
echo "----------------------------------------------------------------------------"
endDate=`date +"%Y-%m-%d %H:%M:%S"`
echo "★[$endDate] db restore Successful"
echo "----------------------------------------------------------------------------"