# 运维实践
以一种便捷和节省成本的实践方式(不是最佳实践)，部署OneX到一个Linxu服务器为例。

基本策略

1. 只需要一台服务器,将数据库、接口、前端部署在一台服务器;
2. 只需要一个域名,通过nginx完成接口访问和前端访问的访问代理;
3. 具备基本的数据备份功能,以防服务器出现奔溃、过期等异常情况的出现;

## 服务器准备
1. 准备Linux服务器
建议购买[阿里云](https://ecs-buy.aliyun.com/wizard#/prepay/cn-hangzhou)  
预算充足选择[通用型](https://help.aliyun.com/document_detail/108490.html),预算有限则选择[突发性能实例](https://help.aliyun.com/document_detail/59977.html)
操作系统选择CentOS 7.X    
配置不低于1C2G1M,建议2C4G2M+


2. 在阿里云后台该Linux的服务实例安全组中，配置以下放行规则   
22(远程访问用)       
8888(宝塔端口,后续可以修改)       
3306(数据库端口,稳定运行可以关闭)
80(http访问端口)
443(https访问端口)


3. 配置DNS解析
在阿里云[域名解析](https://dns.console.aliyun.com/#/dns/domainList)对应的域名中
添加一条A记录sub.foo.com,记录值为上述服务器的公网IP地址

## 配置服务器(宝塔)

1. 安装[宝塔面板](https://www.bt.cn/)   
通过SSH工具,如[xshell](https://www.portablesoft.org/xshell-xftp-6-integrated/)远程连接上述服务器   
执行安装命令```yum install -y wget && wget -O install.sh http://download.bt.cn/install/install_6.0.sh && sh install.sh``
安装完成后会得到宝塔面板的访问路径和帐号
```
Bt-Panel: http://ip:8888/123456
username: username
password: pwd
```
  
  
2. 安装必须软件		
BtPanel-软件商城     
安装所需软件Nginx 1.x/MySQL 5.x/Tomcat 8.x

3. 配置Mysql-设置Mysql为不区分大小写   
BtPanel-软件商城-Mysql-设置-配置修改      
在[mysqld]节点添加配置lower_case_table_names = 1,然后重启服务
     
     
4. 添加数据库	
BtPanel-数据库      
添加业务数据库


5. 配置防火墙
BtPanel-安全      
将上面提到的的阿里云安全策略中的放行端口加入到防火墙放行列表中


5. 添加应用网站	
BtPanel-网站      
添加一个站点sub.foo.com,数据库和ftp都不需要创建


6. 网站添加接口代理     
BtPanel-网站(sub.foo.com)-设置-反向代理     
添加目录为/oneapi,目标地址为http://127.0.0.1:8800的代理      
目的是将sub.foo.com/oneapi的访问代理到http://127.0.0.1:8800/oneapi

7. 网站添加全局代理     
BtPanel-网站(sub.foo.com)-设置-反向代理  
添加目录为/,目标地址为http://127.0.0.1的代理      
然后修改配置文件为
```
location /
{
    try_files /$uri /$uri/ /index.html$args;
}
```
目的是为了解决Vue的router问题

## 部署应用

1. 部署前端应用   
将前端vue编译后的文件上传至/www/wwwroot/sub.foo.com         
访问sub.foo.com即可看到前端页面


2. 部署接口
将接口编译后的jar文件上传至/www/wwwroot/oneapi目录    
将[start.sh](start.sh)上传至/www/wwwroot/oneapi目录    
修改start.sh文件的可执行权限

## 数据库自动备份(Optional)

1. 准备备份用OSS Bucket
在[OSS](https://oss.console.aliyun.com/bucket)创建一个与上述服务器同区域、私有读写权限、存储类型为归档存储(节省成本)的Bucket。       
根据实际情况,同一个客户或者同一个阿里云帐号下可以共用一个数据备份用OSS,无须每个服务器或者每个客户创建一个bucket。      
记录下OSS bucket名称、[AccessKey](https://usercenter.console.aliyun.com/#/manage/ak),后续需要使用。


2. BtPanel-软件商城     
安装所需软件[阿里云OSS]


3. BtPanel-软件商城-阿里云OSS-账户设置     
配置步骤4中的OSS bucket信息


4. BtPanel-计划任务      
添加一个备份数据库任务,根据需要定时备份业务数据库   
测试执行后在OSS中检查是否备份成功


## 配置SSL证书(Optional)

1. 申请SSL证书
在[SSL证书管理中心](https://yundun.console.aliyun.com/?p=cas#/overview/cn-hangzhou)购买一个[单域名免费证书](https://common-buy.aliyun.com/?commodityCode=cas)
购买成功后申请证书配置域名为sub.foo.com,审核通过后,下载nginx证书


2. BtPanel-网站-(sub.foo.com)-设置-SSL 
填入步骤5中下载的key和pem内容,保存后即可        
如开启强制HTTPS,前端的接口配置也应该是https的地址
