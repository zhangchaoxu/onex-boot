package com.nb6868.onexboot.api.modules.shop.controller;

import com.nb6868.onexboot.api.common.annotation.WxWebAuth;
import com.nb6868.onexboot.api.modules.shop.dto.GoodsDTO;
import com.nb6868.onexboot.api.modules.shop.dto.OrderDTO;
import com.nb6868.onexboot.api.modules.shop.service.GoodsService;
import com.nb6868.onexboot.api.modules.shop.service.OrderService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.validator.AssertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * 商城html页面
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Controller("ShopHtml")
@RequestMapping("/shop/html")
@Api(tags = "shop html")
public class HtmlController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    @ApiOperation("商品详情页面")
    @GetMapping("goods/info")
    public String goodsInfo(ModelMap map, @NotNull(message = "{id.require}") @RequestParam Long id) {
        GoodsDTO data = goodsService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        map.put("data", data);
        return "shop/goods-info";
    }

    @WxWebAuth
    @ApiOperation("订单详情页面")
    @GetMapping("order/info")
    public String orderInfo(HttpServletRequest request, ModelMap map, @NotNull(message = "{id.require}") @RequestParam Long id) {
        OrderDTO data = orderService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        map.put("openid", request.getSession().getAttribute(UcConst.WX_SESSION_OPEN_ID));
        map.put("data", data);
        return "shop/order-info";
    }

}
