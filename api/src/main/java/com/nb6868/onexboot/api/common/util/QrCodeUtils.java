package com.nb6868.onexboot.api.common.util;

import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * qrcode 工具类
 * see {https://github.com/kenglxn/QRGen/}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class QrCodeUtils {

    protected final HashMap<EncodeHintType, Object> hints = new HashMap<>();

    protected Writer qrWriter;

    protected int width = 125;

    protected int height = 125;

    /**
     * 文件格式
     * JPG, GIF, PNG, BMP
     */
    protected String imageFormat = "PNG";

    public static final MatrixToImageConfig DEFAULT_CONFIG = new MatrixToImageConfig();

    protected final String text;

    protected MatrixToImageConfig matrixToImageConfig = DEFAULT_CONFIG;

    protected QrCodeUtils(String text) {
        this.text = text;
        qrWriter = new QRCodeWriter();
    }

    /**
     * Create a QR code from the given text.
     *
     * @param text the text to encode to a new QRCode, this may fail if the text is too large. <br>
     * @return the QRCode object    <br>
     */
    public static QrCodeUtils from(String text) {
        return new QrCodeUtils(text);
    }

    /**
     * Overrides the imageFormat from its default
     *
     * @param imageFormat image format
     * @return the current QRCode object
     */
    public QrCodeUtils to(String imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    /**
     * Overrides the size of the qr from its default 125x125
     *
     * @param width  the width in pixels
     * @param height the height in pixels
     * @return the current QRCode object
     */
    public QrCodeUtils withSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Overrides the default charset by supplying a {@link com.google.zxing.EncodeHintType#CHARACTER_SET} hint to {@link
     * com.google.zxing.qrcode.QRCodeWriter#encode}
     *
     * @param charset the charset as string, e.g. UTF-8
     * @return the current QRCode object
     */
    public QrCodeUtils withCharset(String charset) {
        return withHint(EncodeHintType.CHARACTER_SET, charset);
    }

    /**
     * Overrides the default error correction by supplying a {@link com.google.zxing.EncodeHintType#ERROR_CORRECTION} hint to
     * {@link com.google.zxing.qrcode.QRCodeWriter#encode}
     *
     * @param level the error correction level to use by {@link com.google.zxing.qrcode.QRCodeWriter#encode}
     * @return the current QRCode object
     */
    public QrCodeUtils withErrorCorrection(ErrorCorrectionLevel level) {
        return withHint(EncodeHintType.ERROR_CORRECTION, level);
    }

    /**
     * Sets hint to {@link com.google.zxing.qrcode.QRCodeWriter#encode}
     *
     * @param hintType the hintType to set
     * @param value    the concrete value to set
     * @return the current QRCode object
     */
    public QrCodeUtils withHint(EncodeHintType hintType, Object value) {
        hints.put(hintType, value);
        return this;
    }

    public File file() {
        File file;
        try {
            file = createTempFile();
            MatrixToImageWriter.writeToPath(createMatrix(text), imageFormat, file.toPath(), matrixToImageConfig);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create QR image from text due to underlying exception", e);
        }

        return file;
    }

    public File file(String name) {
        File file;
        try {
            file = createTempFile(name);
            MatrixToImageWriter.writeToPath(createMatrix(text), imageFormat, file.toPath(), matrixToImageConfig);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create QR image from text due to underlying exception", e);
        }

        return file;
    }

    protected void writeToStream(OutputStream stream) throws IOException, WriterException {
        MatrixToImageWriter.writeToStream(createMatrix(text), imageFormat, stream, matrixToImageConfig);
    }

    public QrCodeUtils withColor(int onColor, int offColor) {
        matrixToImageConfig = new MatrixToImageConfig(onColor, offColor);
        return this;
    }

    //

    /**
     * returns a {@link ByteArrayOutputStream} representation of the QR code
     *
     * @return qrcode as stream
     */
    public ByteArrayOutputStream stream() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            writeToStream(stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create QR image from text due to underlying exception", e);
        }

        return stream;
    }

    /**
     * writes a representation of the QR code to the supplied  {@link OutputStream}
     *
     * @param stream the {@link OutputStream} to write QR Code to
     */
    public void writeTo(OutputStream stream) {
        try {
            writeToStream(stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create QR image from text due to underlying exception", e);
        }
    }

    public BitMatrix createMatrix(String text) throws WriterException {
        return qrWriter.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, width, height, hints);
    }

    protected File createTempFile() throws IOException {
        File file = File.createTempFile("QRCode", "." + imageFormat.toLowerCase());
        file.deleteOnExit();
        return file;
    }

    protected File createTempFile(String name) throws IOException {
        File file = File.createTempFile(name, "." + imageFormat.toLowerCase());
        file.deleteOnExit();
        return file;
    }

    public Writer getQrWriter() {
        return qrWriter;
    }

    public void setQrWriter(Writer qrWriter) {
        this.qrWriter = qrWriter;
    }

}
