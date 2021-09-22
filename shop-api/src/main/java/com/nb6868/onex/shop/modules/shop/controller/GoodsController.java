package com.nb6868.onex.shop.modules.shop.controller;

import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.DataSqlScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.shop.modules.shop.dao.GoodsDao;
import com.nb6868.onex.shop.modules.shop.dto.GoodsDTO;
import com.nb6868.onex.shop.modules.shop.dto.ReceiverDTO;
import com.nb6868.onex.shop.modules.shop.entity.GoodsEntity;
import com.nb6868.onex.shop.modules.shop.service.GoodsService;
import com.nb6868.onex.shop.shiro.SecurityUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController("ShopGoodsController")
@RequestMapping("/shop/goods")
@Validated
@Slf4j
@Api(tags="商品")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("page")
    @AccessControl("/page")
    @ApiOperation("分页")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<GoodsDTO> page = null;//goodsService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("/info")
    @AccessControl("/info")
    @ApiOperation("信息")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        GoodsEntity entity = goodsService.getById(id);
        AssertUtils.isNull(entity, ErrorCode.DB_RECORD_EXISTS);
        // 转成dto
        GoodsDTO dto = ConvertUtils.sourceToTarget(entity, GoodsDTO.class);

        return new Result<>().success(dto);
    }


}
