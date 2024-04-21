package com.benefitj.core.file;


//import jdk.internal.misc.FileSystemOption;

import java.nio.file.WatchEvent;

public enum SensitivityWatchEventModifier implements WatchEvent.Modifier {
  /**
   * High sensitivity.
   */
  HIGH("FileSystemOption.SENSITIVITY_HIGH", 2),
  /**
   * Medium sensitivity.
   */
  MEDIUM("FileSystemOption.SENSITIVITY_MEDIUM", 10),
  /**
   * Low sensitivity.
   */
  LOW("FileSystemOption.SENSITIVITY_LOW", 30);

  /**
   * Returns the sensitivity in seconds.
   */
  public int sensitivityValueInSeconds() {
    return sensitivity;
  }

  private final int sensitivity;
  private SensitivityWatchEventModifier(String option,
                                        int sensitivity) {
    this.sensitivity = sensitivity;
    //option.register(this, sensitivity);
  }
}

