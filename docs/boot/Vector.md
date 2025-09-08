## Vector向量数据库

### Milvus
#### 设置密码
milvus默认无需密码，为了安全建议设置密码，设置后默认账号root，默认密码Milvus。
可以使用代码或者工具(Attu)做密码的修改
```shell
# 进入docker
docker exec -it <container_id> /bin/bash
# 进入目录
cd configs/
# 修改配置文件
sed -i 's/authorizationEnabled: false/authorizationEnabled: true/' milvus.yaml
# 重启docker
docker restart <container_id>
```
