package com.nb6868.onexboot.api.region;

import cn.afterturn.easypoi.csv.entity.CsvImportParams;
import cn.afterturn.easypoi.csv.imports.CsvImportService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nb6868.onexboot.api.modules.sys.dao.RegionDao;
import com.nb6868.onexboot.api.modules.sys.entity.RegionEntity;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 区域测试
 * see {https://github.com/xiangyuecn/AreaCity-JsSpider-StatsGov/}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegionTest {

    @Resource
    private RegionDao regionDao;

    /**
     * 导入4级区域数据,【四级】省市区镇
     */
    @Test
    public void importDataLevel4() throws IOException {
        // 先删除所有数据
        File file = new File("c://ok_data_level4.csv");
        CsvImportParams params = new CsvImportParams();
        List<RegionOkDataLevel> list =  new CsvImportService().readExcel(FileUtils.openInputStream(file), RegionOkDataLevel.class, params, null);
        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        for (RegionOkDataLevel item : list) {
            RegionEntity entity = ConvertUtils.sourceToTarget(item, RegionEntity.class);
            regionDao.insert(entity);
        }
        System.out.println("导入完成");
    }

    /**
     * 导入3级区域中心点和边界
     */
    @Test
    public void importGeo() throws IOException {
        File file = new File("c://ok_geo.csv");
        CsvImportParams params = new CsvImportParams();
        List<RegionOkGeo> list =  new CsvImportService().readExcel(FileUtils.openInputStream(file), RegionOkGeo.class, params, null);
        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        for (RegionOkGeo item : list) {
            regionDao.update(new RegionEntity(), new UpdateWrapper<RegionEntity>().eq("id", item.getId()).set("geo", item.getGeo()).set("polyline", item.getPolygon()));
        }
        System.out.println("导入GEO完成");
    }

}
