# Linux Cheat Sheet

### 查看centos版本
```shell
cat /etc/redhat-release
```

### 删除大文件
直接删除大文件,可能会因为文件被程序占用,文件被删除了,但是磁盘空间继续占用    
需要用一个空文件来替换
```shell
touch ./blank.txt 
rsync -a --delete-before --progress --stats ./blank.txt ./nohup.out
rm ./blank.txt
```

### 查看磁盘占用
```shell
df -h
```

### 查看本地IP
```shell
ip addr
```

### 查看出口IP
```shell
curl ifconfig.me
```

### 安装软件
比如cronolog
```shell
yum install -y cronolog httpd
```

### 启动java springboot程序
使用cronolog按小时分割日志
```shell
nohup java -Dspring.profiles.active=prod -jar api.jar --server.port=8080 --server.servlet.context-path=/api 2>&1 | cronolog ./log/%Y-%m-%d_%H.out >> /dev/null &
```

### 防火墙设置
查看防火墙策略
```shell
iptables -nvL
```

开放防火墙访问
```shell
iptables -I INPUT 4 -p tcp -m state --state NEW -m tcp --dport 3306 -j ACCEPT
iptables -I INPUT 4 -p tcp -m state --state NEW -m tcp --dport 9000:9100 -j ACCEPT
```

生效设置
```shell
service iptables save
```
在centos7中若提示`The service command supports only basic LSB actions (start, stop, restart, try-restart, reload, force-reload, status). For other actions, please try to use systemctl`则需要执行以此操作
```shell
#  关闭防火墙
systemctl stop firewalld
# 安装 iptables 服务
yum install iptables-services
# 设置 iptables 服务开机启动
systemctl enable iptables    
# 启动iptables   
systemctl start iptables
# 保存iptables配置** 
service iptables save
```

### 开机启动
```
1. 编辑/etc/rc.d/rc.local,加入需要执行的命令
2. 添加权限chmod +x /etc/rc.d/rc.local
3. 重启reboot
4. 启动日志可以查看这里/var/log/boot.log
```

### 修改本地日志保存时长,比如半年
1. 编辑文件
```shell
vi /etc/logrotate.conf
```
将其中
```shell
# keep 4 weeks worth of backlogs
rotate 26

......

# no packages own wtmp and btmp -- we'll rotate them here
/var/log/wtmp {
    monthly
    create 0664 root utmp
        minsize 1M
    rotate 3
}

/var/log/btmp {
    missingok
    monthly
    create 0600 root utmp
    rotate 1
}
```

2. 重启服务
```shell
sudo systemctl restart rsyslog
# service syslog restart
```

### 关闭端口
1. 查找端口占用
```shell
netstat -anp|grep {port}
```

2. 杀死占用进程PID
```shell
kill -9 {pid}
```

### 修改密码长度
1. 查找端口占用
```shell
netstat -anp|grep {port}
```

2. 杀死占用进程PID
```shell
kill -9 {pid}
```

### 删除部分无法删除的文件,比如.user.ini
先解除文件不可更动属性，之后就可以修改/删除
```shell
chattr -i .user.ini
```