package com.benefitj.core.cmd;

import com.benefitj.core.CatchUtils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class KeystoreUtils {

  /**
   * Java密钥库(Java Key Store，JKS)KEY_STORE
   */
  public static final String KEY_STORE = "JKS";
  public static final String X509 = "X.509";

  /**
   * BASE64解密
   */
  public static byte[] decryptBase64(String key) {
    return Base64.getDecoder().decode(key);

  }

  /**
   * BASE64加密
   */
  public static String encryptBase64(byte[] key) {
    return Base64.getEncoder().encodeToString(key).replace("\r", "").replace("\n", "");
  }

  /**
   * 获得KeyStore
   */
  private static KeyStore getKeyStore(String keyStorePath, String password) {
    return CatchUtils.ignore(() -> {
      try (final FileInputStream is = new FileInputStream(keyStorePath);) {
        KeyStore ks = KeyStore.getInstance(KEY_STORE);
        ks.load(is, password.toCharArray());
        return ks;
      }
    });
  }

  /**
   * 由KeyStore获得私钥
   */
  private static PrivateKey getPrivateKey(String keyStorePath, String alias, String storePass, String keyPass) {
    return CatchUtils.ignore(() -> {
      KeyStore ks = getKeyStore(keyStorePath, storePass);
      return (PrivateKey) ks.getKey(alias, keyPass.toCharArray());
    });
  }

  /**
   * 由Certificate获得公钥
   *
   * @param keyStorePath KeyStore路径
   * @param alias        别名
   * @param storePass    KeyStore访问密码
   */
  private static PublicKey getPublicKey(String keyStorePath, String alias, String storePass) {
    return CatchUtils.ignore(() -> {
      KeyStore ks = getKeyStore(keyStorePath, storePass);
      return ks.getCertificate(alias).getPublicKey();
    });
  }

  /**
   * 从KeyStore中获取公钥，并经BASE64编码
   */
  public static String getStrPublicKey(String keyStorePath, String alias, String storePass) {
    PublicKey key = getPublicKey(keyStorePath, alias, storePass);
    return encryptBase64(key.getEncoded());
  }

  /**
   * 获取经BASE64编码后的私钥
   */
  public static String getStrPrivateKey(String keyStorePath, String alias, String storePass, String keyPass) {
    PrivateKey key = getPrivateKey(keyStorePath, alias, storePass, keyPass);
    return encryptBase64(key.getEncoded());
  }

}
