package com.nb6868.onex.modules.tms.controller;

import com.nb6868.onex.booster.pojo.Kv;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.modules.tms.service.WaybillItemService;
import com.nb6868.onex.modules.tms.service.WaybillService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * TMS首页数据数据
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("tms/index")
@Validated
public class IndexController {

    @Autowired
    private WaybillService waybillService;
    @Autowired
    private WaybillItemService waybillItemService;

    @GetMapping("count")
    @ApiOperation("统计数据")
    public Result<?> count(@RequestParam Map<String, Object> params) {
        int waybillCount = waybillService.count(params);
        int waybillItemCount = waybillItemService.count(params);
        params.put("status", 0);
        int waybillItemNotBoardCount = waybillItemService.count(params);

        Kv data = Kv.init()
                .set("waybillCount", waybillCount)
                .set("waybillItemCount", waybillItemCount)
                .set("waybillItemNotBoardCount", waybillItemNotBoardCount);
        return new Result<>().success(data);
    }

}
