package com.nb6868.onexboot.api.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
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
            Graphics2D g = baseImage.createGraphics();
            // 考虑到logo图片贴到二维码中，建议大小不要超过二维码的1/5;
            int width = baseImage.getWidth() / 5;
            int height = baseImage.getWidth() / 5;
            // logo起始位置，此目的是为logo居中显示
            int x = (baseImage.getWidth() - width) / 2;
            int y = (baseImage.getHeight() - height) / 2;
            // 绘制图
            g.drawImage(watermarkImage, x, y, width, height, null);
            // 给logo画透明边框
            // 构造一个具有指定线条宽度以及 cap 和 join 风格的默认值的实心 BasicStroke
            g.setStroke(new BasicStroke(0));
            g.setColor(new Color(0,0,0,0));
            g.drawRect(x, y, width, height);
            g.dispose();
            // 写入logo图片到二维码
            ImageIO.write(baseImage, FilenameUtils.getExtension(baseImageFile.getName()), overlapImageFile);
            return true;
        } catch (Exception e) {
            log.error("ImageUtils", e);
            return false;
        }
    }

    /**
     * 叠加logo图层
     */
    public static boolean overlapText(File baseImageFile, String waterMarkContent, File overlapImageFile) {
        try {
            // 底图
            BufferedImage baseImage = ImageIO.read(baseImageFile);
            // 文字转图片
            BufferedImage textImage = textToImage(waterMarkContent, baseImage.getWidth(), 50);

            Graphics2D baseImageGraphics = baseImage.createGraphics();
            // 在底图上绘制图片
            // logo起始位置，此目的是为logo居中显示
            int x = (baseImage.getWidth() - textImage.getWidth()) / 2;
            int y = (baseImage.getHeight() - textImage.getHeight()) / 2;
            baseImageGraphics.drawImage(textImage, x, y, textImage.getWidth(), textImage.getHeight(), null);
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
     * 文字转图片
     */
    public static BufferedImage textToImage(String text, int width, int height) {
        BufferedImage textImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) textImage.getGraphics();
        // 开启文字抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 透明背景
        g2.setBackground(new Color(0, 0, 0, 0));
        g2.clearRect(0, 0, width, height);
        g2.setPaint(Color.BLACK);
        FontRenderContext context = g2.getFontRenderContext();
        Font font = new Font("Microsoft YaHei", Font.BOLD, 18);
        g2.setFont(font);
        LineMetrics lineMetrics = font.getLineMetrics(text, context);
        FontMetrics fontMetrics = FontDesignMetrics.getMetrics(font);
        float offset = (width - fontMetrics.stringWidth(text)) / 2;
        float y = (height + lineMetrics.getAscent() - lineMetrics.getDescent() - lineMetrics.getLeading()) / 2;
        g2.drawString(text, (int) offset, (int) y);
        return textImage;
    }
}
