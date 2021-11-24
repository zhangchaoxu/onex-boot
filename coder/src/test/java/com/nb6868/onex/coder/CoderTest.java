package com.nb6868.onex.coder;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import com.nb6868.onex.coder.entity.CodeGenerateConfig;
import com.nb6868.onex.coder.utils.GenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@DisplayName("代码生成器")
public class CoderTest {

    @Test
    @DisplayName("生成代码")
    void generateCode() throws Exception {
        TimeInterval timeInterval = DateUtil.timer();
        String tableNameSearch = "data_booth";
        String path = "C:\\Workspaces\\coderTest\\";
        String zipFile = path + tableNameSearch + "-" + DateUtil.current() + ".zip";
        CodeGenerateConfig config = new CodeGenerateConfig();
        config.setAuthorEmail("zhangchaoxu@gmail.com");
        config.setAuthorName("Charles");
        config.setModuleName("data");
        config.setPackageName("com.govsz.nfjc");
        config.setTablePrefix("data");
        config.setVersion("1.0.0");
        // 获取表列表
        List<Map<String, Object>> tableList = tableSchemaDao.queryTable(tableNameSearch);
        FileUtil.touch(zipFile);
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile));
        for (Map<String, Object> tableMap : tableList) {
            String tableName = MapUtil.getStr(tableMap, "table_name");
            // 查询列信息
            List<Map<String, Object>> columns = tableSchemaDao.queryColumns(tableName);
            // 生成代码
            GenUtils.generatorCode(tableMap, columns, config, zip);
        }
        zip.close();
        log.debug("代码生成执行完成,timeInterval={}", timeInterval.intervalSecond());
        Runtime.getRuntime().exec("cmd /c start explorer " + path);
    }

}
