# 备份mysql数据库
# 下面脚本是删除/www/logs文件夹中，文件名是*.out的，并且最后修改时间超过3天的所有文件
# 记得加到定时任务中
#!/bin/bash
PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:~/bin
export PATH
mysqldump -u用户名 -p密码 库名 | gzip > /data/mysql-bak/`date +%Y-%m-%d_%H%M%S`.sql.gz
echo "----------------------------------------------------------------------------"
endDate=`date +"%Y-%m-%d %H:%M:%S"`
echo "★[$endDate] rm file Successful"
echo "----------------------------------------------------------------------------"