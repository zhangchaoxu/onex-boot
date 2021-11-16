# 本地挂代理后,java请求还是没从代理走
- 起因: 在本地用EasyConnect开启一个VPN代理以后，在IDEA运行java程序，网络请求没有从代理走。
- 解决: 在java启动参数中加上`-Djava.net.preferIPv4Stack=true`
![img](./img/issue-ipv4-stack.png)
