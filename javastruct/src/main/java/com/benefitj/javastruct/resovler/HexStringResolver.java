package com.benefitj.javastruct.resovler;

import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.PrimitiveType;
import com.benefitj.javastruct.field.StructField;

import java.lang.reflect.Field;

/**
 * 16进制字符串转换
 */
public class HexStringResolver extends AbstractFieldResolver<String> {

  public HexStringResolver() {
  }

  public HexStringResolver(boolean local) {
    super(local);
  }

  @Override
  public boolean support(Field field, JavaStructField jsf, PrimitiveType pt) {
    return pt == PrimitiveType.STRING;
  }

  @Override
  public byte[] convert(StructField field, Object value) {
    int size = field.size();
    if (value != null) {
      byte[] bytes = HexUtils.hexToBytes((String) value);
      if (bytes.length == size) {
        return bytes;
      }
      byte[] buf = getCache(size);
      return copy(bytes, srcPos(bytes, size), buf, 0, Math.min(bytes.length, buf.length));
    }
    return getCache(size);
  }

  @Override
  public String parse(StructField field, byte[] data, int position) {
    byte[] buf = copy(data, position, getCache(field.size()), 0, field.size());
    return HexUtils.bytesToHex(buf);
  }

}
