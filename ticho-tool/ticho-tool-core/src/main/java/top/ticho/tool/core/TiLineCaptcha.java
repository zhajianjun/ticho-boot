package top.ticho.tool.core;

import top.ticho.tool.core.exception.TiSysException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 验证码创建
 *
 * @author zhajianjun
 * @date 2025-08-17 15:56
 */
public class TiLineCaptcha {
    /**
     * 图片的宽度
     */
    private final int width;
    /**
     * 图片的高度
     */
    private final int height;
    /**
     * 验证码字符个数
     */
    private final int codeCount;
    /**
     * 验证码干扰线数
     */
    private final int lineCount;
    /**
     * 验证码
     */
    private final String code;
    /**
     * 验证码图片Buffer
     */
    private final BufferedImage buffImg;

    private final Random random;

    public TiLineCaptcha() {
        this(160, 40, 4, 20, null);
    }

    public TiLineCaptcha(int width, int height) {
        this(width, height, 4, 20, null);
    }

    public TiLineCaptcha(int width, int height, int codeCount) {
        this(width, height, codeCount, 20, null);
    }

    public TiLineCaptcha(int width, int height, int codeCount, int lineCount) {
        this(width, height, codeCount, lineCount, null);
    }

    public TiLineCaptcha(int width, int height, int codeCount, int lineCount, String code) {
        this.random = new Random();
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        this.code = code == null ? generateCode() : code;
        this.buffImg = createImage();
    }

    private BufferedImage createImage() {
        // 字体的宽度
        int fontWidth = this.width / this.codeCount;
        // 字体的高度
        int fontHeight = this.height - 5;
        int codeY = this.height - 8;
        BufferedImage buffImg = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffImg.getGraphics();
        // 设置背景色
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, this.width, this.height);

        // 设置字体
        Font font = getFont(fontHeight);
        g.setFont(font);

        // 设置干扰线
        setSomeLines(g);

        // 添加噪点
        float yawpRate = 0.01f;// 噪声率
        int area = (int) (yawpRate * this.width * this.height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(this.width);
            int y = random.nextInt(this.height);

            buffImg.setRGB(x, y, random.nextInt(255));
        }
        for (int i = 0; i < this.codeCount; i++) {
            String strRand = this.code.substring(i, i + 1);
            g.setColor(getRandColor(1, 255));
            // g.drawString(a,x,y);
            // a为要画出来的东西，x和y表示要画的东西最左侧字符的基线位于此图形上下文坐标系的 (x, y) 位置处
            g.drawString(strRand, i * fontWidth + 3, codeY);
        }
        return buffImg;
    }


    private void setSomeLines(Graphics g) {
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(height);
            g.setColor(getRandColor(1, 255));
            g.drawLine(xs, ys, xe, ye);
        }
    }

    /**
     * 得到随机字符
     *
     * @return 指定长度的随机串
     */
    public String generateCode() {
        StringBuilder builder = new StringBuilder();
        int len = TiIdUtil.ALPHABET.length - 1;
        double r;
        for (int i = 0; i < codeCount; i++) {
            r = (Math.random()) * len;
            builder.append(TiIdUtil.ALPHABET[(int) r]);
        }
        return builder.toString();
    }

    /**
     * 得到随机颜色
     *
     * @param fc 范围
     * @param bc 范围
     * @return 颜色
     */
    private Color getRandColor(int fc, int bc) {
        // 给定范围获得随机颜色
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    // 扭曲方法
    private void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private void shearX(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(2);
        int frames = 1;
        int phase = random.nextInt(2);
        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1) * Math
                .sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);

            g.setColor(color);
            g.drawLine((int) d, i, 0, i);
            g.drawLine((int) d + w1, i, w1, i);
        }

    }

    private void shearY(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(40) + 10;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            g.setColor(color);
            g.drawLine(i, (int) d, i, 0);
            g.drawLine(i, (int) d + h1, i, h1);
        }
    }

    /**
     * 产生固定字体
     */
    private Font getFont(int size) {
        return new Font("Fixedsys", Font.BOLD, size);
    }

    /**
     * 产生随机字体
     */
    private Font getRandomFont(int size) {
        Font[] font = new Font[5];
        font[0] = new Font("Ravie", Font.PLAIN, size);
        font[1] = new Font("Antique Olive Compact", Font.PLAIN, size);
        font[2] = new Font("Fixedsys", Font.PLAIN, size);
        font[3] = new Font("Wide Latin", Font.PLAIN, size);
        font[4] = new Font("Gill Sans Ultra Bold", Font.PLAIN, size);
        return font[random.nextInt(5)];
    }

    public void write(OutputStream sos) throws IOException {
        ImageIO.write(buffImg, "png", sos);
        sos.close();
    }

    public byte[] getBytes() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 写入流中
            ImageIO.write(buffImg, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new TiSysException(e);
        }
    }

    public BufferedImage getBuffImg() {
        return buffImg;
    }

    public String getCode() {
        return code.toLowerCase();
    }

}
