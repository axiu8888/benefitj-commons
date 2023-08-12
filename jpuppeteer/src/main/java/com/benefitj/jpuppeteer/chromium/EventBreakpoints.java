package com.benefitj.jpuppeteer.chromium;

/**
 * EventBreakpoints permits setting breakpoints on particular operations and events in targets that run JavaScript but do not
 * have a DOM. JavaScript execution will stop on these operations as if there was a regular breakpoint set. EXPERIMENTAL
 */
@ChromiumApi("EventBreakpoints")
public interface EventBreakpoints {

  /**
   * Removes breakpoint on particular native event.
   *
   * @param eventName string
   *                  Instrumentation name to stop on.
   */
  void removeInstrumentationBreakpoint(String eventName);

  /**
   * Sets breakpoint on particular native event.
   *
   * @param eventName string
   *                  Instrumentation name to stop on.
   */
  void setInstrumentationBreakpoint(String eventName);

}
