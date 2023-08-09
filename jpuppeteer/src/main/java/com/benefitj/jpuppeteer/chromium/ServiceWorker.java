package com.benefitj.jpuppeteer.chromium;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ServiceWorker Domain
 */
@Deprecated
@ChromiumApi("ServiceWorker")
public interface ServiceWorker {


  /**
   * Allowed Values: new, installing, installed, activating, activated, redundant
   */
  public enum ServiceWorkerVersionStatus {
    _new, installing, installed, activating, activated, redundant;

    public static final List<String> values = Collections.unmodifiableList(Arrays.asList(
        "new",
        "installing",
        "installed",
        "activating",
        "activated",
        "redundant"
    ));
  }

}
