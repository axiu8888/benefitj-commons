package com.benefitj.consistenthash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 */
public class MessageDigestHash implements HashFunction {

  private final MessageDigest md;

  private final Map<String, Long> hashCache = new WeakHashMap<>();

  public MessageDigestHash(String algorithm) {
    try {
      this.md = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public long hash(String key) {
    long h;
    synchronized (this) {
      h = hashCache.getOrDefault(key, 0L);
      if (h == 0) {
        md.reset();
        md.update(key.getBytes());
        byte[] digest = md.digest();
        for (int i = 0; i < 4; i++) {
          h <<= 8;
          h |= ((int) digest[i]) & 0xFF;
        }
        hashCache.put(key, h);
      }
    }
    return h;
  }

  public String getAlgorithm() {
    return md.getAlgorithm();
  }
}
