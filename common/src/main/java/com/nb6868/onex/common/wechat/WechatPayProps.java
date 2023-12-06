package com.nb6868.onex.common.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信支付信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Schema(name = "微信支付信息")
public class WechatPayProps implements Serializable {

     @Schema(description = "微信公众号或者小程序的appid")
    private String appId;

     @Schema(description = "微信支付商户号")
    private String mchId;

     @Schema(description = "微信支付商户密钥")
    private String mchKey;

     @Schema(description = "服务商模式下的子商户公众账号ID，普通模式请不要配置")
    private String subAppId;

     @Schema(description = "服务商模式下的子商户号，普通模式请不要配置")
    private String subMchId;

     @Schema(description = "apiclient_cert.p12文件的绝对路径，或者如果放在项目中，请以classpath:开头指定")
    private String keyPath;

    /**
     * https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2
     */
     @Schema(description = "交易类型,JSAPI,APP,NATIVE")
    private String tradeType;

}
