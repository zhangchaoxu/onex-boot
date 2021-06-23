package com.nb6868.onexboot.api.modules.crm.controller;

import cn.hutool.core.map.MapUtil;
import com.nb6868.onexboot.api.common.annotation.DataFilter;
import com.nb6868.onexboot.api.modules.crm.service.BusinessService;
import com.nb6868.onexboot.api.modules.crm.service.ContractService;
import com.nb6868.onexboot.api.modules.crm.service.CustomerService;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.pojo.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页数据
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/crm/index")
@Validated
public class IndexController {

    @Autowired
    CustomerService customerService;
    @Autowired
    BusinessService businessService;
    @Autowired
    ContractService contractService;

    @DataFilter(tenantFilter = true)
    @GetMapping("count")
    @ApiOperation("统计数据")
    public Result<?> count(@RequestParam Map<String, Object> params) {
        int customerCount = customerService.count(params);
        int businessCount = businessService.count(params);
        int contractCount = contractService.count(params);
        // 各个占比
        List<Map<String, Object>> customerSourceCount = customerService.listSourceCount(params);
        List<Map<String, Object>> businessStateCount = businessService.listStateCount(params);
        if (ObjectUtils.isEmpty(MapUtil.getInt(params, "contractYear"))) {
            params.put("contractYear", Year.now().getValue());
        }
        List<Map<String, Object>> contractContractMonthCount = contractService.listContractMonthCount(params);
        List<Map<String, Object>> contractContractMonthCountInYear = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String month = params.get("contractYear").toString() + "-" + (i < 10 ? ("0" + i) : i);
            boolean existed = false;
            for (Map<String, Object> map : contractContractMonthCount) {
                if (map.get("contract_month").toString().equalsIgnoreCase(month)) {
                    contractContractMonthCountInYear.add(map);
                    existed = true;
                    break;
                }
            }
            if (!existed) {
                Map<String, Object> map = new HashMap<>();
                map.put("contract_month", month);
                map.put("count", 0);
                map.put("amount_sum", 0);
                contractContractMonthCountInYear.add(map);
            }
        }

        Kv data = Kv.init()
                .set("customerCount", customerCount)
                .set("businessCount", businessCount)
                .set("contractCount", contractCount)
                .set("customerSourceCount", customerSourceCount)
                .set("contractContractMonthCount", contractContractMonthCountInYear)
                .set("businessStateCount", businessStateCount);
        return new Result<>().success(data);
    }

}
