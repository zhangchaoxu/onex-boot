# MySQL Cheat Sheet

### 大小写敏感
mysql设置为大小写不敏感 
方法：修改/etc/my.cnf文件,在[mysqld]节点加入配置`lower_case_table_names = 1`,然后重启mysql(`service mysqld restart`)

### 复制表结构
```mysql
CREATE TABLE IF NOT EXISTS db1.a LIKE db2.a
```

### 复制表数据(结构一致)
```mysql
NSERT INTO {sourceDbName.sourceTableName} SELECT * FROM {targetDbName.targetTableName}
```

### 复制表数据(结构不一致)
```mysql
NSERT INTO {sourceDbName.sourceTableName} (字段1,字段2) SELECT 字段1,字段2 FROM {targetDbName.targetTableName}
```

### 过滤重复数据
```mysql
SELECT id FROM {tabaleName} WHERE id NOT IN (SELECT  t.id FROM ( SELECT MIN( id ) AS id FROM {tabaleName} GROUP BY `name` ) t)
```

### 删除重复数据
不能边查询边操作，可以先把需要保留的id找出来，然后删除配出这些id后的数据
```mysql
DELETE FROM {tabaleName} WHERE id NOT IN (SELECT  t.id FROM ( SELECT MIN( id ) AS id FROM {tabaleName} GROUP BY `name` ) t)
```
