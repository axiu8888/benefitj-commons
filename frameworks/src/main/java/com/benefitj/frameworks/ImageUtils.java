package com.benefitj.frameworks;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;
import net.sf.image4j.use.Image4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {


  /**
   * 修改图片尺寸
   *
   * @param src  原始图片
   * @param dest 目标图片
   * @param size 修改后的大小
   * @return 返回被修改的图标
   */
  public static File resize(File src, File dest, int size) {
    return resize(src, dest, size, size);
  }

  /**
   * 修改图片尺寸
   *
   * @param src        原始图片
   * @param dest       目标图片
   * @param destWidth  目标宽度
   * @param destHeight 目标高度
   * @return 返回被修改的图标
   */
  public static File resize(File src, File dest, int destWidth, int destHeight) {
    try {
      String ext = FilenameUtils.getExtension(src.getName()).toLowerCase();
      // 缩放
      BufferedImage inputBufImage = ImageIO.read(src);
      ResampleOp resampleOp = new ResampleOp(destWidth, destHeight);// 转换
      resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Oversharpened);
      BufferedImage rescaledTomato = resampleOp.filter(inputBufImage, null);
      ImageIO.write(rescaledTomato, ext, dest);
      return dest;
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 转换为ico
   *
   * @param src  原始文件(png/jpg)
   * @param dest 目标图片
   * @param size 大小
   * @return 返回被转换后的图片
   */

  public static File imgToIco(File src, File dest, int size) {
    final File tmpResize = IOUtils.createFile(SystemUtils.JAVA_IO_TMPDIR, IdUtils.uuid(9) + "__" + src.getName());
    try {
      resize(src, tmpResize, size);
      //png/jpg 转 ico
      Image4j.Convert(tmpResize.getAbsolutePath(), dest.getAbsolutePath());
      return dest;
    } finally {
      IOUtils.delete(tmpResize);
    }
  }

}
