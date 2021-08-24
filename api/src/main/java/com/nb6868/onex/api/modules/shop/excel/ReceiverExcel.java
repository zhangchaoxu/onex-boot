package com.nb6868.onex.api.modules.shop.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收件地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReceiverExcel {

    @Excel(name = "用户id")
    private Long userId;
    @Excel(name = "区域名称,如浙江省,宁波市,鄞州区")
    private String regionName;
    @Excel(name = "区域编号,如33000,33010,33011")
    private String regionCode;
    @Excel(name = "详细门牌号")
    private String address;
    @Excel(name = "收件人")
    private String consignee;
    @Excel(name = "邮编")
    private String zipCode;
    @Excel(name = "收件人手机号")
    private String mobile;
    @Excel(name = "默认项", replace = {"否_0", "是_1", "保密_2"})
    private Integer defaultItem;

}
