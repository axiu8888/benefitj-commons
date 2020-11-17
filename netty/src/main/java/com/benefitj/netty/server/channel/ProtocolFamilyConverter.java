package com.benefitj.netty.server.channel;

import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.internal.SuppressJava6Requirement;

import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;

/**
 * Helper class which convert the {@link InternetProtocolFamily}.
 */
public final class ProtocolFamilyConverter {

  private ProtocolFamilyConverter() {
    // Utility class
  }

  /**
   * Convert the {@link InternetProtocolFamily}. This MUST only be called on jdk version >= 7.
   */
  @SuppressJava6Requirement(reason = "Usage guarded by java version check")
  public static ProtocolFamily convert(InternetProtocolFamily family) {
    switch (family) {
      case IPv4:
        return StandardProtocolFamily.INET;
      case IPv6:
        return StandardProtocolFamily.INET6;
      default:
        throw new IllegalArgumentException();
    }
  }
}
