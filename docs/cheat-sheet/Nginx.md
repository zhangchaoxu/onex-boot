# Nginx Cheat Sheet

### 启停命令
```shell
# 查看版本
nginx -v
# 启动
nginx
# 指定配置文件启动
nginx -c /xxxx/nginx.conf
# 快速关停
nginx -s stop
# 正常关停
nginx -s quit
# 检查配置
nginx -t
# 重载配置
nginx -s reload
```

### 查看nginx位置
```shell
ps -ef|grep nginx
ll /proc/{pid}/exe
```

### 升级版本
参考[nginx进行平滑升级](https://blog.csdn.net/weixin_45414913/article/details/124801803)
```shell
# 下载
wget http://nginx.org/download/nginx-1.22.1.tar.gz
# 解压
tar -zxvf nginx-1.22.1.tar.gz
# 配置(进入解压目录)
./configure
# 编译
make
# 备份原nginx文件，复制编译后的objs/nginx到原nginx中
# 执行升级
make upgrade
```


