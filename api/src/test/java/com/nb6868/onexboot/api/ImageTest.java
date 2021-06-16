package com.nb6868.onexboot.api;

import cn.hutool.core.img.Img;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;

/**
 * 图片功能 测试
 * 注意,如需使用二维码功能，需要手动引入zxing的依赖
 * see https://hutool.cn/docs/#/extra/%E4%BA%8C%E7%BB%B4%E7%A0%81%E5%B7%A5%E5%85%B7-QrCodeUtil
 *
 * @author Charles zhangchaoxu@gmail.com
 */
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImageTest {

    @Test
    @DisplayName("二维码")
    public void generateQrcode() {
        QrCodeUtil.generate("https://hutool.cn/", 300, 300, FileUtil.file("C:\\1234.png"));
        System.out.println("生成二维码成功,识别内容为:" + QrCodeUtil.decode(new File("C:\\1234.png")));
    }

    @Test
    @DisplayName("添加文字水印")
    public void pressText() {
        ImgUtil.pressText(
                FileUtil.file("C:\\1234.png"), //
                FileUtil.file("C:\\1234_q.png"), //
                "版权所有", Color.WHITE, //文字
                new Font("黑体", Font.BOLD, 100), //字体
                0, //x坐标修正值。 默认在中间，偏移量相对于中间偏移
                0, //y坐标修正值。 默认在中间，偏移量相对于中间偏移
                0.8f//透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
        );
    }

    @Test
    @DisplayName("压缩图片")
    public void compress() {
        Img.from(FileUtil.file("C:/1.jpg"))
                .setQuality(0.8)//压缩比率
                .write(FileUtil.file("C:/1_target.jpg"));
    }

}
