package com.benefitj.jpuppeteer.chromium;

import lombok.Data;

public interface Debugger {

  /**
   * Search match for resource.
   */
  @Data
  public class SearchMatch {
    /**
     * Line number in resource content.
     */
    Integer lineNumber;
    /**
     * Line with match content.
     */
    String lineContent;
  }

}
