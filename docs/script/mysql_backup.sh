# 备份mysql数据库
# 下面脚本是备份数据库
# 记得加到定时任务中
#!/bin/bash
PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:~/bin
export PATH
mysqldump -u用户名 -p密码 库名 | gzip > /data/mysql-bak/`date +%Y-%m-%d_%H%M%S`.sql.gz
echo "----------------------------------------------------------------------------"
endDate=`date +"%Y-%m-%d %H:%M:%S"`
echo "★[$endDate] db backup Successful"
echo "----------------------------------------------------------------------------"