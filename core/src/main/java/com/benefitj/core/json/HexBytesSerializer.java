package com.benefitj.core.json;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.benefitj.core.HexUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 自定义序列化器：将 byte[] 转为十六进制字符串
 */
public class HexBytesSerializer extends JsonSerializer<byte[]> implements ObjectWriter<byte[]> {

    public static final HexBytesSerializer INSTANCE = new HexBytesSerializer();

    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }
        byte[] bytes = (byte[]) object;
        jsonWriter.writeString(HexUtils.bytesToHex(bytes));
    }

    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(HexUtils.bytesToHex(value));
    }
}
