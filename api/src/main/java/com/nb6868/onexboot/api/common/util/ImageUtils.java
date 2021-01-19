package com.nb6868.onexboot.api.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 图片工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class ImageUtils {

    /**
     * 叠加logo图层
     *
     * @param baseImageFile 底图文件
     * @param watermarkImageFile 水印图片文件
     * @param overlapImageFile 合并后的图片文件
     * @return
     */
    public static boolean overlapImage(File baseImageFile, File watermarkImageFile, File overlapImageFile) {
        try {
            BufferedImage baseImage = ImageIO.read(baseImageFile);
            BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);
            Graphics2D baseImageGraphics = baseImage.createGraphics();
            // 考虑到logo图片贴到二维码中，建议大小不要超过二维码的1/5;
            int width = baseImage.getWidth() / 5;
            int height = baseImage.getWidth() / 5;
            // logo起始位置，此目的是为logo居中显示
            int x = (baseImage.getWidth() - width) / 2;
            int y = (baseImage.getHeight() - height) / 2;
            // 绘制图
            baseImageGraphics.drawImage(watermarkImage, x, y, width, height, null);
            baseImageGraphics.dispose();
            // 将文件导出
            ImageIO.write(baseImage, FilenameUtils.getExtension(baseImageFile.getName()), overlapImageFile);
            return true;
        } catch (Exception e) {
            log.error("ImageUtils", e);
            return false;
        }
    }

    /**
     * 叠加text图层
     */
    public static boolean overlapText(File baseImageFile, String waterMarkContent, File overlapImageFile) {
        try {
            int fontSize = 15;
            BufferedImage baseImage = ImageIO.read(baseImageFile);
            Graphics2D baseImageGraphics = baseImage.createGraphics();
            // 计算水平位置，尽量居中
            int x = baseImage.getWidth() / 2 - fontSize * waterMarkContent.length() / 2;
            int y = baseImage.getHeight() - fontSize * 3;
            baseImageGraphics.setFont(new Font("Microsoft YaHei", Font.BOLD, fontSize));
            //消除锯齿状
            baseImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            baseImageGraphics.setColor(Color.BLACK);
            baseImageGraphics.drawString(waterMarkContent, x, y);
            baseImageGraphics.dispose();
            // 图片写入文件
            ImageIO.write(baseImage, FilenameUtils.getExtension(baseImageFile.getName()), overlapImageFile);
            return true;
        } catch (Exception e) {
            log.error("ImageUtils", e);
            return false;
        }
    }

    /**
     * 图片压缩
     */
    public static boolean compress(File baseImageFile, File overlapImageFile) {
        // todo
        return true;
        /*try {
            Thumbnails.of(baseImageFile).watermark(new Watermark()).toFile(overlapImageFile);
            return true;
        } catch (Exception e) {
            log.error("ImageUtils", e);
            return false;
        }*/
    }


}
