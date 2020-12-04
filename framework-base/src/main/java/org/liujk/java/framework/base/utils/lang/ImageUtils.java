
package org.liujk.java.framework.base.utils.lang;




import org.apache.commons.io.FileUtils;
import org.springframework.util.Assert;

import javax.annotation.CheckReturnValue;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

/**
 * 说明：
 * <p>
 *
 */
public class ImageUtils {
    public static final int BUFFER_SIZE = 4096;
    public static final int SEED = 0x1F;
    private static float DEFAULT_FACTOR = 0.5f;//默认图片压缩比例

    /**
     * 生成缩略图
     *
     * @param sourceFilePath 源文件路径
     * @param targetFilePath 目标文件路径
     * @param width          目标文件宽
     * @param hight          目标文件高
     *
     * @throws Exception
     */
    public static void generateThumbnails (String sourceFilePath, String targetFilePath, int width, int hight)
            throws Exception {
        BufferedImage srcImage;
        // String ex = fromFileStr.substring(fromFileStr.indexOf("."),fromFileStr.length());
        String imgType = "JPEG";
        if (sourceFilePath.toLowerCase ().endsWith (".png")) {
            imgType = "PNG";
        }

        File targetFile = new File (targetFilePath);
        File sourceFile = new File (sourceFilePath);

        srcImage = ImageIO.read (sourceFile);

        if (width > 0 || hight > 0) {
            srcImage = thumb (srcImage, width, hight);
        }

        ImageIO.write (srcImage, imgType, targetFile);
    }

    /**
     * @param source
     * @param width  目标宽
     * @param height 目标高
     *
     * @return
     */
    public static BufferedImage thumb (BufferedImage source, int width, int height) {

        int type = source.getType ();
        BufferedImage target = null;
        double sx = (double) width / source.getWidth ();
        double sy = (double) height / source.getHeight ();
        //这里想实现在width,height范围内实现等比缩放。如果不需要等比缩放则将下面的if else语句注释即可.
        if (sx > sy) {
            sx = sy;
            width = (int) (sx * source.getWidth ());
        } else {
            sy = sx;
            height = (int) (sy * source.getHeight ());
        }
        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = source.getColorModel ();
            WritableRaster raster = cm.createCompatibleWritableRaster (width, height);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied ();
            target = new BufferedImage (cm, raster, alphaPremultiplied, null);
        } else {
            target = new BufferedImage (width, height, type);
        }

        Graphics2D g = target.createGraphics ();

        g.setColor (Color.WHITE);
        g.setRenderingHint (RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage (source, AffineTransform.getScaleInstance (sx, sy));
        g.dispose ();

        return target;
    }

    /**
     * 生成缩略图文件名
     */
    public static String madeThumbFileName (String originalFilename, int width, int height) {

        return "t_" + width + "_" + height + "_" + originalFilename;
    }

    /**
     * 生成文件名
     */
    public static String madeFileName (String originalFilename) {

        return System.currentTimeMillis ()
                + originalFilename.substring (originalFilename.lastIndexOf ("."), originalFilename.length ());
    }

    /**
     * 获取图片域名
     */
    public static String getCustomerImgDomain (String domain, String folder, String customerId) {

        return domain + folder + "/" + customerId + "/";
    }

    /**
     * 图片压缩 默认按照长/宽 50% 压缩
     *
     * @param srcFilePath  输入图片路径
     * @param destFilePath 输出图片路径
     *
     * @throws IOException
     */
    public static void compressResize (String srcFilePath, String destFilePath)
            throws IOException {
        compressResize (srcFilePath, destFilePath, 0, 0);
    }

    /**
     * 图片压缩
     *
     * @param srcFilePath  输入图片路径
     * @param destFilePath 输出图片路径
     * @param width        压缩图片宽度
     * @param height       压缩图片高度
     *
     * @throws IOException
     */
    public static void compressResize (String srcFilePath, String destFilePath, int width, int height)
            throws IOException {
        compressResize (new File (srcFilePath), new File (destFilePath), width, height, true);
    }


    /**
     * 图片压缩 按短边比例缩放
     *
     * @param srcFile         输入图片文件
     * @param destFile        输出图片文件
     * @param width           压缩图片宽度
     * @param height          压缩图片高度
     * @param resizeByMaxSide true按短边比例缩放, false按长边比例缩放
     *
     * @throws IOException
     * @see ImageUtil#compressResize(String, String, int, int)
     */
    public static void compressResize (File srcFile, File destFile, int width, int height,
                                       boolean resizeByMaxSide)
            throws IOException {
        Assert.isTrue (srcFile != null, "The param srcFile must be not null!");
        Assert.isTrue (srcFile != null, "The param destFile must be not null!");
        Assert.isTrue (srcFile.exists (), "The source file [" + srcFile + "] must exists!");

        //读取原始文件长度和宽度
        BufferedImage bi = ImageIO.read (srcFile);
        double srcWidth = bi.getWidth ();
        double srcHeight = bi.getHeight ();

        //计算压缩比
        float factorW = width > 0 ? (float) width / (float) srcWidth : 0;
        float factorH = height > 0 ? (float) height / (float) srcHeight : 0;

        float factor;
        if (resizeByMaxSide) {
            factor = factorH < factorW ? factorH : factorW;
        } else {
            factor = factorH > factorW ? factorH : factorW;
        }
        compressResize (srcFile, destFile, factor);
    }

    /**
     * 图片压缩
     *
     * @param srcFile  输入图片
     * @param destFile 输出图片
     * @param factor   输出图片压缩比 介于0~1之间
     *
     * @throws IOException
     */
    public static void compressResize (File srcFile, File destFile, float factor) throws IOException {
        BufferedImage bi = ImageIO.read (srcFile);
        if (factor > 1) {
            factor = 1;
        } else if (factor <= 0) {
            factor = DEFAULT_FACTOR;
        }

        BufferedImage resizedImg = compress (bi, bi.getType (),
                                             Math.round (bi.getWidth () * factor),
                                             Math.round (bi.getHeight () * factor),
                                             factor, factor);
        FileUtils.forceMkdirParent (destFile);
        ImageIO.write (resizedImg, "jpg", destFile);
    }

    /**
     * @param sbi       需要压缩的图片缓冲
     * @param imageType 图片类型
     * @param dWidth    目标图片宽度
     * @param dHeight   目标图片高度
     * @param fWidth    需要转换的图片宽度 / 原来图片宽度
     * @param fHeight   需要转换的图片高度 / 原来图片高度
     *
     * @return
     */
    private static BufferedImage compress (BufferedImage sbi, int imageType, int dWidth,
                                           int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage (dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics ();
            AffineTransform at = AffineTransform.getScaleInstance (fWidth, fHeight);
            g.drawRenderedImage (sbi, at);
        }
        return dbi;
    }


    /**
     * 将字节数组的数据加密，写进路径的文件
     *
     * @param in
     * @param out
     *
     * @throws IOException
     */
    public static void encrypt (byte[] in, File out) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream (in);
        OutputStream outStream = new BufferedOutputStream (new FileOutputStream (out));
        doEncrypt (inStream, outStream);
    }

    /**
     * 将字节数组的数据解密，写进路径的文件
     *
     * @param in
     * @param out
     *
     * @throws IOException
     */
    public static void decrypt (byte[] in, File out) throws IOException {
        encrypt (in, out);
    }

    /**
     * 将字节数组的数据加密，返回输入流
     *
     * @param in
     *
     * @return byte[]
     * @throws IOException
     */
    public static byte[] encrypt (byte[] in) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream (in);
        doEncrypt (inStream).read (in);
        return in;
    }

    /**
     * 将字节数组的数据解密，返回输入流
     *
     * @param in
     *
     * @return byte[]
     * @throws IOException
     */
    public static byte[] decrypt (byte[] in) throws IOException {
        return encrypt (in);
    }

    /**
     * 将字节数组的数据加密，然后写进路径名字的文件
     *
     * @param inFilePath
     * @param outFilePath
     *
     * @throws IOException
     */
    public static void encrypt (String inFilePath, String outFilePath) throws IOException {
        encrypt (new File (inFilePath), new File (outFilePath));
    }

    /**
     * 将字节数组的数据解密，然后写进路径名字的文件
     *
     * @param inFilePath
     * @param outFilePath
     *
     * @throws IOException
     */
    public static void decrypt (String inFilePath, String outFilePath) throws IOException {
        encrypt (new File (inFilePath), new File (outFilePath));
    }

    /**
     * 将输文件内容加密，写进输出的文件
     *
     * @param inputFile
     * @param outputFile
     *
     * @throws IOException
     */
    public static void encrypt (File inputFile, File outputFile) throws IOException {
        doEncrypt (new FileInputStream (inputFile), new FileOutputStream (outputFile));
    }

    /**
     * 将输文件内容解密，写进输出的文件
     *
     * @param inputFile
     * @param outputFile
     *
     * @throws IOException
     */
    public static void decrypt (File inputFile, File outputFile) throws IOException {
        doEncrypt (new FileInputStream (inputFile), new FileOutputStream (outputFile));
    }

    /**
     * 将输文件内容加密，返回输入流
     *
     * @param inputFile
     *
     * @return InputStream
     * @throws IOException
     */
    public static InputStream encrypt (File inputFile) throws IOException {
        return doEncrypt (new FileInputStream (inputFile));
    }

    /**
     * 将输文件内容解密，返回输入流
     *
     * @param inputFile
     *
     * @return InputStream
     * @throws IOException
     */
    public static InputStream decrypt (File inputFile) throws IOException {
        return doEncrypt (new FileInputStream (inputFile));
    }

    /**
     * 将输入流的内容加密，写进输出流
     *
     * @param in
     * @param out
     *
     * @throws IOException
     */
    public static void encrypt (InputStream in, OutputStream out) throws IOException {
        doEncrypt (in, out);
    }

    /**
     * 将输入流的内容解密，写进输出流
     *
     * @param in
     * @param out
     *
     * @throws IOException
     */
    public static void decrypt (InputStream in, OutputStream out) throws IOException {
        doEncrypt (in, out);
    }

    /**
     * 将输入流的内容加密，返回输入流
     *
     * @param in
     *
     * @return InputStream
     * @throws IOException
     */
    public static InputStream encrypt (InputStream in) throws IOException {
        return doEncrypt (in);
    }

    /**
     * 将输入流的内容解密，返回输入流
     *
     * @param in
     *
     * @return InputStream
     * @throws IOException
     */
    public static InputStream decrypt (InputStream in) throws IOException {
        return doEncrypt (in);
    }


    /**
     * 对文件前100字节异或，破坏文件头使文件无法打开
     *
     * @param inputStream
     * @param outputStream
     *
     * @return int 操作的数据大小
     * @throws IOException
     */
    private static int doEncrypt (InputStream inputStream, OutputStream outputStream)
            throws IOException {
        int byteCount = 0;
        int encryptLength = 100;
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int bytesRead = -1; (bytesRead = inputStream.read (buffer)) != -1; ) {
                //异或
                if (byteCount <= 0) {
                    encryptLength = buffer.length < encryptLength ? buffer.length : encryptLength;
                    for (int i = 0; i < encryptLength; i++) {
                        buffer[i] ^= SEED;
                    }
                }
                outputStream.write (buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            outputStream.flush ();
            return byteCount;
        } finally {
            if (inputStream != null) {
                inputStream.close ();
            }
            if (outputStream != null) {
                outputStream.close ();
            }
        }
    }

    /**
     * 对文件前100字节异或
     *
     * @param inputStream 输入流
     *
     * @return InputStream 加密后的inputStream
     * @throws IOException IO异常
     */
    private static InputStream doEncrypt (InputStream inputStream) throws IOException {
        return new XorInputStream (inputStream, SEED);
    }

    /**
     * 对输入流前一段作异或运算
     */
    public static class XorInputStream extends FilterInputStream {

        /**
         * 最大异或长度
         */
        protected final int encryptLength = 100;
        public int seed = 0x1F;
        protected int count = 0;
        /**
         * 是否开启异或运算
         */
        private boolean on = true;

        protected XorInputStream (InputStream in, int seed) {
            super (in);
            setSeed (seed);
        }

        @Override
        public int read () throws IOException {
            int ch = this.in.read ();
            if (on && ch != -1 && count < encryptLength) {
                ch ^= seed;
                count++;
            }
            return ch;
        }

        @Override
        public int read (byte[] b) throws IOException {
            return this.read (b, 0, b.length);
        }

        @Override
        public int read (byte[] b, int off, int len) throws IOException {
            int result = this.in.read (b, off, len);
            if (on && result != -1) {
                xor (b, off, result);
            }
            return result;
        }

        public void xor (byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new IllegalArgumentException ("No input buffer given");
            }
            if (b.length - off < len) {
                throw new IllegalArgumentException ("Input buffer too short");
            }
            if (encryptLength > count) {
                int xorLen = encryptLength - count;
                int eben = len < xorLen ? len : xorLen;
                for (int i = off; i < off + eben; ++i) {
                    b[i] ^= seed;
                    count++;
                }
            }
        }

        @Override
        @CheckReturnValue
        public boolean markSupported () {
            return false;
        }

        @Override
        public void mark (int readlimit) {
        }

        @Override
        public void reset () throws IOException {
            throw new IOException ("reset not supported");
        }

        public boolean isOn () {
            return on;
        }

        public void setOn (boolean on) {
            this.on = on;
        }

        public int getSeed () {
            return seed;
        }

        public void setSeed (int seed) {
            this.seed = seed;
        }
    }
}
