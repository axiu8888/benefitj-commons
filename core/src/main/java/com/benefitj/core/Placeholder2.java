package com.benefitj.core;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 占位符替换
 * 拷贝自 {@link org.springframework.util.PropertyPlaceholderHelper}
 */
public class Placeholder2 {

  static final SingletonSupplier<Placeholder2> default1 = SingletonSupplier.of(() -> new Placeholder2("${", "}"));
  static final SingletonSupplier<Placeholder2> default2 = SingletonSupplier.of(() -> new Placeholder2("#{", "}"));

  public static Placeholder2 getDefault1() {
    return default1.get();
  }

  public static Placeholder2 getDefault2() {
    return default2.get();
  }

  private static final Map<String, String> wellKnownSimplePrefixes = new HashMap<>(4);

  static {
    wellKnownSimplePrefixes.put("}", "{");
    wellKnownSimplePrefixes.put("]", "[");
    wellKnownSimplePrefixes.put(")", "(");
  }

  static ThreadLocal<Map<String, ?>> mapLocal = ThreadLocal.withInitial(Collections::emptyMap);
  static final PlaceholderResolver default_resolver = new PlaceholderResolver() {
    @javax.annotation.Nullable
    @Override
    public String resolvePlaceholder(String placeholderName) {
      Object value = mapLocal.get().get(placeholderName);
      return value != null ? String.valueOf(value) : null;
    }
  };


  private final String placeholderPrefix;

  private final String placeholderSuffix;

  private final String simplePrefix;

  @javax.annotation.Nullable
  private final String valueSeparator;

  private final boolean ignoreUnresolvablePlaceholders;


  /**
   * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
   * Unresolvable placeholders are ignored.
   *
   * @param placeholderPrefix the prefix that denotes the start of a placeholder
   * @param placeholderSuffix the suffix that denotes the end of a placeholder
   */
  public Placeholder2(String placeholderPrefix, String placeholderSuffix) {
    this(placeholderPrefix, placeholderSuffix, null, true);
  }

  /**
   * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
   *
   * @param placeholderPrefix              the prefix that denotes the start of a placeholder
   * @param placeholderSuffix              the suffix that denotes the end of a placeholder
   * @param valueSeparator                 the separating character between the placeholder variable
   *                                       and the associated default value, if any
   * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should
   *                                       be ignored ({@code true}) or cause an exception ({@code false})
   */
  public Placeholder2(String placeholderPrefix, String placeholderSuffix,
                      @javax.annotation.Nullable String valueSeparator, boolean ignoreUnresolvablePlaceholders) {
    notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
    notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
    this.placeholderPrefix = placeholderPrefix;
    this.placeholderSuffix = placeholderSuffix;
    String simplePrefixForSuffix = wellKnownSimplePrefixes.get(this.placeholderSuffix);
    if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
      this.simplePrefix = simplePrefixForSuffix;
    } else {
      this.simplePrefix = this.placeholderPrefix;
    }
    this.valueSeparator = valueSeparator;
    this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
  }

  /**
   * Replaces all placeholders of format {@code ${name}} with the corresponding
   * property from the supplied {@link Properties}.
   *
   * @param value      the value containing the placeholders to be replaced
   * @param properties the {@code Properties} to use for replacement
   * @return the supplied value with placeholders replaced inline
   */
  public String replace(String value, final Map<String, Object> properties) {
    notNull(properties, "'properties' must not be null");
    try {
      mapLocal.set(properties);
      return replace(value, default_resolver);
    } finally {
      mapLocal.remove();
    }
  }

  /**
   * Replaces all placeholders of format {@code ${name}} with the value returned
   * from the supplied {@link PlaceholderResolver}.
   *
   * @param value               the value containing the placeholders to be replaced
   * @param placeholderResolver the {@code PlaceholderResolver} to use for replacement
   * @return the supplied value with placeholders replaced inline
   */
  public String replace(String value, PlaceholderResolver placeholderResolver) {
    notNull(value, "'value' must not be null");
    return parseStringValue(value, placeholderResolver, null);
  }

  protected String parseStringValue(
      String value, PlaceholderResolver placeholderResolver, @javax.annotation.Nullable Set<String> visitedPlaceholders) {

    int startIndex = value.indexOf(this.placeholderPrefix);
    if (startIndex == -1) {
      return value;
    }

    StringBuilder result = new StringBuilder(value);
    while (startIndex != -1) {
      int endIndex = findPlaceholderEndIndex(result, startIndex);
      if (endIndex != -1) {
        String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
        String originalPlaceholder = placeholder;
        if (visitedPlaceholders == null) {
          visitedPlaceholders = new HashSet<>(4);
        }
        if (!visitedPlaceholders.add(originalPlaceholder)) {
          throw new IllegalArgumentException(
              "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
        }
        // Recursive invocation, parsing placeholders contained in the placeholder key.
        placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
        // Now obtain the value for the fully resolved key...
        String propVal = placeholderResolver.resolvePlaceholder(placeholder);
        if (propVal == null && this.valueSeparator != null) {
          int separatorIndex = placeholder.indexOf(this.valueSeparator);
          if (separatorIndex != -1) {
            String actualPlaceholder = placeholder.substring(0, separatorIndex);
            String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
            propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
            if (propVal == null) {
              propVal = defaultValue;
            }
          }
        }
        if (propVal != null) {
          // Recursive invocation, parsing placeholders contained in the
          // previously resolved placeholder value.
          propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
          result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
          startIndex = result.indexOf(this.placeholderPrefix, startIndex + propVal.length());
        } else if (this.ignoreUnresolvablePlaceholders) {
          // Proceed with unprocessed value.
          startIndex = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
        } else {
          throw new IllegalArgumentException("Could not resolve placeholder '" +
              placeholder + "'" + " in value \"" + value + "\"");
        }
        visitedPlaceholders.remove(originalPlaceholder);
      } else {
        startIndex = -1;
      }
    }
    return result.toString();
  }

  private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
    int index = startIndex + this.placeholderPrefix.length();
    int withinNestedPlaceholder = 0;
    while (index < buf.length()) {
      if (substringMatch(buf, index, this.placeholderSuffix)) {
        if (withinNestedPlaceholder > 0) {
          withinNestedPlaceholder--;
          index = index + this.placeholderSuffix.length();
        } else {
          return index;
        }
      } else if (substringMatch(buf, index, this.simplePrefix)) {
        withinNestedPlaceholder++;
        index = index + this.simplePrefix.length();
      } else {
        index++;
      }
    }
    return -1;
  }


  /**
   * Strategy interface used to resolve replacement values for placeholders contained in Strings.
   */
  @FunctionalInterface
  public interface PlaceholderResolver {

    /**
     * Resolve the supplied placeholder name to the replacement value.
     *
     * @param placeholderName the name of the placeholder to resolve
     * @return the replacement value, or {@code null} if no replacement is to be made
     */
    @javax.annotation.Nullable
    String resolvePlaceholder(String placeholderName);
  }

  static void notNull(@Nullable Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
    if (index + substring.length() > str.length()) {
      return false;
    }
    for (int i = 0; i < substring.length(); i++) {
      if (str.charAt(index + i) != substring.charAt(i)) {
        return false;
      }
    }
    return true;
  }
}
