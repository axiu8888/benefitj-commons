package com.benefitj.http;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * SSL检验
 */
public class SSL {

  public static final HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> true;

  public static final X509TrustManager X_509_TRUST_MANAGER = new X509TrustManager() {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  };

  public static SSLSocketFactory getSSLSocketFactory() {
    return getSSLSocketFactory(X_509_TRUST_MANAGER);
  }

  public static SSLSocketFactory getSSLSocketFactory(TrustManager manager) {
    try {
      SSLContext ctx = SSLContext.getInstance("SSL");
      ctx.init(null, new TrustManager[]{manager}, new SecureRandom());
      return ctx.getSocketFactory();
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new IllegalStateException(e);
    }
  }

}
