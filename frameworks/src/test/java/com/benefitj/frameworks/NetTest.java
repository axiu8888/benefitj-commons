package com.benefitj.frameworks;


import com.benefitj.core.DateFmtter;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;


public class NetTest extends BaseTest {

  @Test
  void testNtp() throws IOException {
    NTPUDPClient client = new NTPUDPClient();
    client.setDefaultTimeout(2000); // 设置超时时间（毫秒）
    client.open();

//    String timeServer = "cn.ntp.org.cn"; // NTP 服务器地址
    String timeServer = "time.windows.com"; // NTP 服务器地址
    // 查询时间服务器
    InetAddress address = InetAddress.getByName(timeServer);
    TimeInfo timeInfo = client.getTime(address);
    // 获取返回的时间
    long currentTimeMillis = timeInfo.getMessage().getTransmitTimeStamp().getTime();
    Date date = new Date(currentTimeMillis);
    log.error("从时间服务器获取的时间: {}", DateFmtter.fmtS(date));
    client.close();
  }


}
