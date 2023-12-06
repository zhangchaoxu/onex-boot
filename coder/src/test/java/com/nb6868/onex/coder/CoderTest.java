package com.nb6868.onex.coder;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import com.nb6868.onex.coder.entity.CodeGenerateConfig;
import com.nb6868.onex.coder.utils.GenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Slf4j
@DisplayName("代码生成器")
public class CoderTest {

    @Test
    @DisplayName("生成代码")
    void generateCode() throws Exception {
        TimeInterval timeInterval = DateUtil.timer();
        String tableNames = "data_booth";
        String path = "C:\\Workspaces\\coderTest\\";
        String zipFile = path + tableNames + "-" + DateUtil.current() + ".zip";
        CodeGenerateConfig config = new CodeGenerateConfig();
        config.setAuthorEmail("zhangchaoxu@gmail.com");
        config.setAuthorName("Charles");
        config.setModuleName("data");
        config.setPackageName("com.govsz.nfjc");
        config.setTablePrefix("data");
        config.setVersion("1.0.0");
        // 获取表列表
        DataSource ds = new SimpleDataSource("jdbc:mysql://121.43.118.210:3306/nfjc", "nfjc", "cn355nA7YhJpzYMZ");
        List<Entity> tableList = Db.use(ds).query("SELECT table_name as table_name, engine as engine, table_comment as table_comment, create_time as create_time FROM information_schema.tables WHERE table_schema = (select database()) and table_name like ?", "%" + tableNames + "%");
        FileUtil.touch(zipFile);
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile));
        for (Map<String, Object> tableMap : tableList) {
            String tableName = MapUtil.getStr(tableMap, "table_name");
            // 查询列信息
            List<Entity> columns = Db.use(ds).query("SELECT column_name as column_name, data_type as data_type, column_comment as column_comment, column_key as column_key, extra as extra FROM information_schema.columns WHERE table_schema = (select database()) and table_name = ? order by ordinal_position", tableName);
            // 生成代码
            GenUtils.generatorCode(tableMap, columns, config, zip);
        }
        zip.close();
        log.debug("代码生成执行完成,timeInterval={}", timeInterval.intervalSecond());
        RuntimeUtil.exec("cmd /c start explorer " + path);
    }

}
