package com.nb6868.onex.api;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.api.modules.sys.dao.TableSchemaDao;
import com.nb6868.onex.coder.entity.CodeGenerateConfig;
import com.nb6868.onex.coder.utils.GenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class CoderTest {

    @Resource
    TableSchemaDao tableSchemaDao;

    @Test
    @DisplayName("加载所有表")
    void loadAllTables() {
        String tableNameSearch = "cms";
        tableSchemaDao.queryTable(tableNameSearch).forEach(tableMap -> {
            // 打印出所有表
            log.error("table={},json={}", MapUtil.getStr(tableMap, "table_name"), JSONUtil.toJsonStr(tableMap));
        });
    }

    @Test
    @DisplayName("生成代码")
    void generateCode() throws Exception {
        CodeGenerateConfig config = new CodeGenerateConfig();
        config.setAuthorEmail("zhangchaoxu@gmail.com");
        config.setAuthorName("Charles");
        config.setModuleName("cms");
        config.setPackageName("com.govsz.plus");
        config.setTablePrefix("cms");
        config.setVersion("1.0.0");
        String tableNameSearch = "cms";
        // 获取表列表
        List<Map<String, Object>> tableList = tableSchemaDao.queryTable(tableNameSearch);
        FileUtil.touch("c:\\Workspaces\\test.zip");
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream("c:\\Workspaces\\test.zip"));
        for (Map<String, Object> tableMap : tableList) {
            String tableName = MapUtil.getStr(tableMap, "table_name");
            // 查询列信息
            List<Map<String, Object>> columns = tableSchemaDao.queryColumns(tableName);
            // 生成代码
            GenUtils.generatorCode(tableMap, columns, config, zip);
        }
        try {
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.error("完成");
    }

}

