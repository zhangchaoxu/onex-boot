# 多租户
OneX在设计之初，考虑了多租户的情况，在预留了租户表，同时在部分表中预留了tenant_code字段。      
实际在落地过程中发现要实现多租户有太多需要考虑的细节问题，都需要于实际业务结合，很难做一个统一的多租户设计。

### 问题点
1. 
