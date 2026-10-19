package com.benefitj.core.json;


import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.benefitj.core.HexUtils;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 自定义反序列化器：将十六进制字符串转回 byte[]
 */
public class HexBytesDeserializer extends JsonDeserializer<byte[]> implements ObjectReader<byte[]> {

    public static final HexBytesDeserializer INSTANCE = new HexBytesDeserializer();

    @Override
    public byte[] readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        // 如果遇到的是 null 或空值，直接返回 null
        if (jsonReader.nextIfNull()) {
            return null;
        }
        // 读取字符串形式的十六进制内容
        String hex = jsonReader.readString();
        return HexUtils.hexToBytes(hex);
    }


    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String text = p.getValueAsString();
        if (text == null || text.isEmpty()) {
            return null;
        }
        return HexUtils.hexToBytes(text);
    }

}

