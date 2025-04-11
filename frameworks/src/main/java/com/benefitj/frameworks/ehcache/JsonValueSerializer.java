package com.benefitj.frameworks.ehcache;


import com.benefitj.core.JsonUtils;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;

import java.nio.ByteBuffer;

/**
 * json 序列化
 */
public class JsonValueSerializer<T> implements Serializer<T> {

  private final Class<T> clazz;

  public JsonValueSerializer(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public ByteBuffer serialize(T object) throws SerializerException {
    if (object instanceof byte[]) return ByteBuffer.wrap((byte[]) object);
    if (object instanceof String) return ByteBuffer.wrap(((String) object).getBytes());
    return ByteBuffer.wrap(JsonUtils.toJsonBytes(object));
  }

  @Override
  public T read(ByteBuffer binary) throws ClassNotFoundException, SerializerException {
    byte[] bytes = new byte[binary.remaining()];
    binary.get(bytes);
    if (clazz == byte[].class) return (T) bytes;
    if (clazz == String.class) return (T) new String(bytes);
    return JsonUtils.fromJson(bytes, clazz);
  }

  @Override
  public boolean equals(T object, ByteBuffer binary) throws SerializerException, ClassNotFoundException {
    return object.equals(read(binary));
  }

}

