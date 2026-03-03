package com.benefitj.core.json;


import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.benefitj.core.HexUtils;

import java.lang.reflect.Type;


/**
 * 自定义序列化器：将 byte[] 转为十六进制字符串
 */
public class HexBytesSerializer implements ObjectReader<byte[]> {

  public static final HexBytesSerializer INSTANCE = new HexBytesSerializer();

  @Override
  public byte[] readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
    if (jsonReader.nextIfNull()) {
      return null;
    }
    // 读取字符串值（即我们写入的十六进制字符串）
    String hexString = jsonReader.readString();
    // 将十六进制字符串转换为 byte[]
    return HexUtils.hexToBytes(hexString);
  }

}

/// **
// * 自定义序列化器：将 byte[] 转为十六进制字符串
// */
//public class HexBytesSerializer implements ObjectSerializer {
//
//  public static final HexBytesSerializer INSTANCE = new HexBytesSerializer();
//
//  @Override
//  public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
//    if (object == null) {
//      serializer.writeNull();
//      return;
//    }
//    byte[] bytes = (byte[]) object;
//    String hex = HexUtils.bytesToHex(bytes);
//    serializer.write(hex); // 直接输出字符串
//  }
//}
