package com.nb6868.onex.api.region;

import cn.afterturn.easypoi.csv.entity.CsvImportParams;
import cn.afterturn.easypoi.csv.imports.CsvImportService;
import cn.hutool.core.io.file.FileReader;
import com.nb6868.onex.api.modules.sys.dao.RegionDao;
import com.nb6868.onex.api.modules.sys.entity.RegionEntity;
import com.nb6868.onex.api.modules.sys.service.RegionService;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 区域测试
 * see {https://github.com/xiangyuecn/AreaCity-JsSpider-StatsGov/}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegionTest {

    @Resource
    private RegionDao regionDao;
    @Autowired
    private RegionService regionService;
    @Autowired
    private DbUtils dbUtils;

    /**
     * 导入4级区域数据,【四级】省市区镇
     */
    @Test
    public void importDataLevel4() throws IOException {
        String file = "c://ok_data_level4.csv";
        CsvImportParams params = new CsvImportParams();
        List<RegionOkDataLevel> list =  new CsvImportService().readExcel(new FileReader(file).getInputStream(), RegionOkDataLevel.class, params, null);
        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        for (RegionOkDataLevel item : list) {
            RegionEntity entity = ConvertUtils.sourceToTarget(item, RegionEntity.class);
            regionDao.insert(entity);
        }
        log.info("4级区域数据-导入完成");
    }

    /**
     * 导入3级区域中心点和边界
     */
    /*@Test
    public void importGeo() throws IOException {
        // 先清空数据
        regionDao.update(new RegionEntity(), new UpdateWrapper<RegionEntity>().set("geo", null).set("polyline", null));
        File file = new File("c://ok_geo.csv");
        CsvImportParams params = new CsvImportParams();
        List<RegionOkGeo> list =  new CsvImportService().readExcel(FileUtils.openInputStream(file), RegionOkGeo.class, params, null);
        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        for (RegionOkGeo item : list) {
            //regionDao.update(new RegionEntity(), new UpdateWrapper<RegionEntity>().eq("id", item.getId()).set("geo", item.getGeo()).set("polyline", item.getPolygon()));
        }
        log.info("3级区域数据边界-更新完成");
    }*/

    /**
     * 导入3级区域中心点和边界
     */
    @Test
    public void importGeo() {
        // 先清空数据
        //regionDao.update(new RegionEntity(), new UpdateWrapper<RegionEntity>().set("geo", null).set("polygon", null));
        String sql = "select * from ok_geo";
        List<Map<String, Object>> list = dbUtils.executeQuerySql(sql);
        log.info("list size = " + list.size());
        list.forEach(item -> {
            log.info(item.toString());
            regionService.update().set("geo", item.get("geo").toString().trim().replaceAll(" ", ","))
                    .set("polygon", item.get("polygon").toString().trim().replaceAll(",", "_").replaceAll(" ", ","))
                    .eq("id", item.get("id")).update();
        });
        regionService.update().set("geo", null).eq("geo", "EMPTY").update();
        regionService.update().set("polygon", null).eq("polygon", "EMPTY").update();
        log.info("3级区域数据边界-更新完成");
    }

}
