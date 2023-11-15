package com.caipiao.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Description: tlwcloud
 * Created by yj198 on 2020/12/9 21:00
 */
@Slf4j
public class ImageUtil {

    /**
     * 导入本地图片到缓冲区
     */
    public static BufferedImage loadImageLocal(String imgName) {
        try {
            return ImageIO.read(new File(imgName));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 剪裁成正方形
     */
    public static BufferedImage getSque(BufferedImage bi) {
        int init_width = bi.getWidth();
        int init_height = bi.getHeight();
        if (init_width != init_height){
            int width_height = 0;
            int x = 0;
            int y = 0;
            if (init_width > init_height) {
                width_height = init_height;//原图是宽大于高的长方形
                x = (init_width-init_height)/2;
                y = 0;
            } else if (init_width < init_height) {
                width_height = init_width;//原图是高大于宽的长方形
                y = (init_height-init_width)/2;
                x = 0;
            }
            bi = bi.getSubimage(x, y, width_height, width_height);
        }
        return bi;
    }



    /**
     * 生成新图片到本地
     */
    public static void writeImageLocal(String newImage, BufferedImage img) {
        if (newImage != null && img != null) {
            try {
                File outputfile = new File(newImage);
                ImageIO.write(img, "png", outputfile);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }





    /**
     * @param srcImage     原图片
     * @param width      期望宽
     * @param height     期望高
     * @param equalScale 是否等比例缩放
     */
    public static BufferedImage reSize(BufferedImage srcImage, int width,
                              int height, boolean equalScale) {

        if (srcImage != null) {
            // targetW，targetH分别表示目标长和宽
            BufferedImage target = null;
            double sx = (double) width / srcImage.getWidth();
            double sy = (double) height / srcImage.getHeight();
            // 等比缩放
            if (equalScale) {
                if (sx > sy) {
                    sx = sy;
                    width = (int) (sx * srcImage.getWidth());
                } else {
                    sy = sx;
                    height = (int) (sy * srcImage.getHeight());
                }
            }
            log.debug("destImg size=" + width + "X" + height);
            ColorModel cm = srcImage.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();

            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
            Graphics2D g = target.createGraphics();
            // smoother than exlax:
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawRenderedImage(srcImage, AffineTransform.getScaleInstance(sx, sy));
            g.dispose();
            return target;
        }

        return null;
    }






    public static void main(String[] args) {
        BufferedImage bufferedImage = loadImageLocal("c:\\Users\\yj198\\Desktop\\Dingtalk_20220418141122.jpg");

        BufferedImage sque = getSque(bufferedImage);

        BufferedImage bufferedImage1 = reSize(sque, 400, 400, true);

        writeImageLocal("c:\\Users\\yj198\\Desktop\\Dingtalk_20220418141122_sque.jpg", bufferedImage1);

    }
}
