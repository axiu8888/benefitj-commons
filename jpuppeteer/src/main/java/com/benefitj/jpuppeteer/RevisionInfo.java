package com.benefitj.jpuppeteer;

import com.benefitj.core.cmd.SystemOS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.File;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RevisionInfo {

  /**
   * 版本
   */
  private String revision;
  /**
   * 下载的目录
   */
  private File folder;

  /**
   * 下载的URL路径
   */
  private String url;

  /**
   * 平台 win linux mac
   */
  @Builder.Default
  private SystemOS platform = SystemOS.getLocale();
  /**
   * 目前支持两种产品：chrome or firefix
   */
  @Builder.Default
  private Product product = Product.chrome;

}
