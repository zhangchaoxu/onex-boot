package com.nb6868.onexboot.api.common.util;

import com.google.common.collect.Maps;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * qrcode 工具类
 * see {https://github.com/kenglxn/QRGen/}
 * {@link com.github.binarywang.utils.qrcode.QrcodeUtils}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class QrCodeUtils {

    protected final HashMap<EncodeHintType, Object> hints = new HashMap<>();

    protected Writer qrWriter;

    /**
     * 二维码长宽尺寸
     */
    protected int width = 125;
    protected int height = 125;

    /**
     * 文件格式
     * JPG, GIF, PNG, BMP
     */
    protected String imageFormat = "PNG";

    /**
     * 二维码文字
     */
    protected String text;

    protected MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig();

    public QrCodeUtils(String text) {
        this.text = text;
        qrWriter = new QRCodeWriter();
    }

    /**
     * Create a QR code from the given text.
     */
    public static QrCodeUtils text(String text) {
        return new QrCodeUtils(text);
    }

    /**
     * Overrides the imageFormat from its default
     */
    public QrCodeUtils withImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    /**
     * Overrides the size of the qr from its default 125x125
     */
    public QrCodeUtils withSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Sets image config to {@link com.google.zxing.client.j2se.MatrixToImageConfig}
     */
    public QrCodeUtils withImageConfig(MatrixToImageConfig imageConfig) {
        this.matrixToImageConfig = imageConfig;
        return this;
    }

    /**
     * Sets hint to {@link com.google.zxing.qrcode.QRCodeWriter#encode}
     */
    public QrCodeUtils withHint(EncodeHintType hintType, Object value) {
        hints.put(hintType, value);
        return this;
    }

    public BitMatrix createMatrix(String text) {
        try {
            return qrWriter.encode(new String(text.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1), com.google.zxing.BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            log.error("QrCodeUtils", e);
            return null;
        }
    }

    /**
     * write to stream
     */
    protected boolean writeToStream(OutputStream stream) {
        try {
            MatrixToImageWriter.writeToStream(createMatrix(text), imageFormat, stream, matrixToImageConfig);
        } catch (IOException ioe) {
            log.error("QrCodeUtils", ioe);
            return false;
        }
        return true;
    }

    /**
     * write to file
     */
    public boolean writeToFile(File file) {
        try {
            MatrixToImageWriter.writeToPath(createMatrix(text), imageFormat, file.toPath(), matrixToImageConfig);
        } catch (IOException ioe) {
            log.error("QrCodeUtils", ioe);
            return false;
        }
        return true;
    }

    public ByteArrayOutputStream toStream() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        boolean result = writeToStream(stream);
        return result ? stream : null;
    }

    public File toFile() {
        File file;
        try {
            file = File.createTempFile("QRCode", "." + imageFormat.toLowerCase());
            file.deleteOnExit();
        } catch (IOException ioe) {
            log.error("QrCodeUtils", ioe);
            return null;
        }
        boolean result = writeToFile(file);
        return result ? file : null;
    }

    /**
     * 解码二维码内容
     */
    public static String decodeQrcode(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            Binarizer binarizer = new HybridBinarizer(new BufferedImageLuminanceSource(image));
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = Maps.newEnumMap(DecodeHintType.class);
            hints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
            return new MultiFormatReader().decode(binaryBitmap, hints).getText();
        } catch (IOException | NotFoundException e) {
            log.error("QrCodeUtils", e);
            return null;
        }
    }

}
