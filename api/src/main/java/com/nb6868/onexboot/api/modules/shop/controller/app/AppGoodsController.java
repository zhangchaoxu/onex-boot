package com.nb6868.onexboot.api.modules.shop.controller.app;

import com.nb6868.onexboot.api.common.annotation.DataFilter;
import com.nb6868.onexboot.api.modules.shop.dto.GoodsDTO;
import com.nb6868.onexboot.api.modules.shop.service.GoodsService;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 商品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("app/shop/goods")
@Validated
@Api(tags="商品")
public class AppGoodsController {
    @Autowired
    private GoodsService goodsService;

    @DataFilter(tableAlias = "shop_goods", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<GoodsDTO> page = goodsService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        GoodsDTO data = goodsService.getDtoById(id);

        return new Result<>().success(data);
    }
}
