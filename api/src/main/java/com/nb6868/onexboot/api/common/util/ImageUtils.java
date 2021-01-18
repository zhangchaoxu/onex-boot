package com.nb6868.onexboot.api.common.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 图片工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ImageUtils {

    /**
     * 叠加logo图层
     */
    public static void overlapImage(BufferedImage image, String format, String imagePath, File logoFile) throws IOException {
        /*try {
            BufferedImage logo = ImageIO.read(logoFile);
            Graphics2D g = image.createGraphics();
            // 考虑到logo图片贴到二维码中，建议大小不要超过二维码的1/5;
            int width = image.getWidth() / 5;
            int height = image.getWidth() / 5;
            // logo起始位置，此目的是为logo居中显示
            int x = (image.getWidth() - width) / 2;
            int y = (image.getHeight() - height) / 2;
            // 绘制图
            g.drawImage(logo, x, y, width, height, null);
            // 给logo画透明边框
            // 构造一个具有指定线条宽度以及 cap 和 join 风格的默认值的实心 BasicStroke
            g.setStroke(new BasicStroke(0));
            g.setColor(new Color(0,0,0,0));
            g.drawRect(x, y, width, height);
            g.dispose();
            // 写入logo图片到二维码
            ImageIO.write(image, format, new File(imagePath));
        } catch (Exception e) {
            throw new IOException("二维码添加logo时发生异常！", e);
        }*/
    }

}
