# ElasticSearch

## Install
1. 下载 [release地址](https://www.elastic.co/cn/downloads/)
`wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.16.2-linux-x86_64.tar.gz`
2. 解压
`tar -zxvf elasticsearch-7.16.2-linux-x86_64.tar.gz`
3. 执行,后台执行
`./bin/elasticsearch -d`
4. 验证,访问ip:9002,注意防火墙
5. 重启方法
```
# jps或者ps -ef|grep elastic查看进程
jps
# kill
kill -9 {pid}
# 启动
./elasticsearch -d
```

### 注意点
1. ES不允许使用root账户执行,否则会提示错误can not run elasticsearch as root,需要创建用户`adduser elasticsearch`
2. es的执行目录以及文件目录需要授予改用户权限`chown -R elasticsearch:elasticsearch /program/elasticsearch`,否则会提示Access Denined
3. 若提示`max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]`,需要在/etc/sysctl.conf中加一行`vm.max_map_count = 262144`,然后重启`/sbin/sysctl -p`
4. 若访问默认端口无数据,需要在config/elasticsearch.yml中修改`network.host: 0.0.0.0`
5. 数据和log配置均在config/elasticsearch.yml
