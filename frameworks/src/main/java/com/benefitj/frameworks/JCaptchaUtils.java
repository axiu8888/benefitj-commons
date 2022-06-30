///*
// * Copyright 2017-2018 the original author(https://github.com/wj596)
// *
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * </p>
// */
//package com.benefitj.frameworks;
//
//import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
//import com.octo.captcha.component.image.color.RandomListColorGenerator;
//import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
//import com.octo.captcha.component.image.textpaster.NonLinearTextPaster;
//import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
//import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
//import com.octo.captcha.engine.GenericCaptchaEngine;
//import com.octo.captcha.image.gimpy.GimpyFactory;
//import com.octo.captcha.service.CaptchaServiceException;
//import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
//import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
//import com.octo.captcha.service.image.ImageCaptchaService;
//
//import javax.imageio.ImageIO;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.io.OutputStream;
//
///**
// * 验证码工具
// */
//public final class JCaptchaUtils {
//  /**
//   * 随机字符
//   */
//  private static final String ACCEPTED_CHARS = "1234567890abcdefghijklmnopkuvwxyzABCDEFGHIJKLMNOPKUVWXYZ";
//
//  /**
//   * 获取验证码服务
//   */
//  private static ImageCaptchaService getInstance() {
//    return JCaptchaHolder.INSTANCE;
//  }
//
//  /**
//   * 生成验证码
//   */
//  public static BufferedImage generateCaptcha(HttpServletRequest request) {
//    return getInstance().getImageChallengeForID(request.getSession(true).getId());
//  }
//
//  /**
//   * 返回二维码
//   *
//   * @param request    请求
//   * @param response   响应
//   * @param formatName 图片格式: JPG/PNG/GIF
//   * @return
//   */
//  public static boolean response(
//      HttpServletRequest request, HttpServletResponse response, String formatName) {
//    response.setHeader("Cache-Control", "no-store");
//    response.setHeader("Pragma", "no-cache");
//    response.setDateHeader("Expires", 0);
//    response.setContentType("image/jpeg");
//
//    BufferedImage image = generateCaptcha(request);
//    try (OutputStream out = response.getOutputStream()) {
//      ImageIO.write(image, formatName, out);
//      out.flush();
//      return true;
//    } catch (IOException e) {
//      // e.printStackTrace();
//      /* ignore */
//      return false;
//    }
//  }
//
//  /**
//   * 验证码校验
//   */
//  public static boolean validateCaptcha(HttpServletRequest request, String jcaptcha) {
//    try {
//      String captchaID = request.getSession().getId();
//      return getInstance().validateResponseForID(captchaID, jcaptcha);
//    } catch (CaptchaServiceException e) {
//      return false;
//    }
//  }
//
//  /**
//   * 验证码服务实例持有者
//   */
//  private static final class JCaptchaHolder {
//
//    private static volatile ImageCaptchaService INSTANCE;
//
//    static {
//      Color[] colors =
//          new Color[]{new Color(23, 170, 27), new Color(220, 34, 11), new Color(23, 67, 172)};
//      RandomListColorGenerator generator = new RandomListColorGenerator(colors);
//      NonLinearTextPaster textPaster = new NonLinearTextPaster(5, 5, generator);
//      UniColorBackgroundGenerator backgroundGenerator =
//          new UniColorBackgroundGenerator(90, 30, Color.white);
//      Font[] fonts = new Font[]{new Font("Arial", 20, 20)};
//      RandomFontGenerator fontGernerator = new RandomFontGenerator(20, 20, fonts);
//      ComposedWordToImage composedWordToImage =
//          new ComposedWordToImage(fontGernerator, backgroundGenerator, textPaster);
//
//      GimpyFactory gimpyFactory =
//          new GimpyFactory(new RandomWordGenerator(ACCEPTED_CHARS), composedWordToImage);
//      GimpyFactory[] gimpyFactories = new GimpyFactory[]{gimpyFactory};
//      GenericCaptchaEngine engine = new GenericCaptchaEngine(gimpyFactories);
//
//      FastHashMapCaptchaStore store = new FastHashMapCaptchaStore();
//      INSTANCE = new DefaultManageableImageCaptchaService(store, engine, 180, 100000, 75000);
//    }
//  }
//}
