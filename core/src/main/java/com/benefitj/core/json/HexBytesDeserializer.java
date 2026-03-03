package com.benefitj.core.json;


import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.benefitj.core.HexUtils;

import java.lang.reflect.Type;

/**
 * 自定义反序列化器：将十六进制字符串转回 byte[]
 */
public class HexBytesDeserializer implements ObjectWriter<byte[]> {

  public static final HexBytesDeserializer INSTANCE = new HexBytesDeserializer();

  @Override
  public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
    if (object == null) {
      jsonWriter.writeNull();
      return;
    }
    byte[] bytes = (byte[]) object;
    // 将 byte[] 转换为十六进制字符串
    String hexString = HexUtils.bytesToHex(bytes, false);
    jsonWriter.writeString(hexString);
  }


}

///**
// * 自定义反序列化器：将十六进制字符串转回 byte[]
// */
//public class HexBytesDeserializer implements ObjectDeserializer {
//
//  public static final HexBytesDeserializer INSTANCE = new HexBytesDeserializer();
//
//  @Override
//  public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
//    // 解析出的值可能是字符串类型
//    Object value = parser.parse();
//    if (value == null) {
//      return null;
//    }
//    String hex = value.toString();
//    return (T) HexUtils.hexToBytes(hex);
//  }
//
//  @Override
//  public int getFastMatchToken() {
//    return 0; // 通常返回0即可
//  }
//}

