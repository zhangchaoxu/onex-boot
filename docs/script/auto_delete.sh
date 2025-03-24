# 删除指定目录中，指定更新时间，指定文件格式的脚本
# 下面脚本是删除/www/logs文件夹中，文件名是*.out的，并且最后修改时间超过3天的所有文件
# 记得加到定时任务中
#!/bin/bash
PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:~/bin
export PATH
find /www/xxx/logs -mtime +3 -name "*.out" -exec rm -rf {} \;
echo "----------------------------------------------------------------------------"
endDate=`date +"%Y-%m-%d %H:%M:%S"`
echo "★[$endDate] rm file Successful"
echo "----------------------------------------------------------------------------"