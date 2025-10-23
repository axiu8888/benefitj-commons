package com.benefitj.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * XML 解析和格式化
 *
 * <p><b>完整类定义：</b></p>
 *
 * <pre>{@literal
 * @Data
 * @JsonIgnoreProperties(ignoreUnknown = true)
 * @JacksonXmlRootElement(localName = "Request")
 * public class XmlRequest {
 *
 *     // 请求头部分
 *     @JacksonXmlProperty(localName = "Header")
 *     Header header;
 *
 *     // 请求体部分
 *     @JacksonXmlProperty(localName = "Body")
 *     Body body;
 *
 * }
 * }</pre>
 */
public class XmlUtils {


  static final XmlMapper xmlMapper = new XmlMapper();

  public static String toXml(Object target) {
    return toXml(target, false);
  }

  public static String toXml(Object target, boolean pretty) {
    try {
      synchronized (xmlMapper) {
        if (pretty) xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        else xmlMapper.disable(SerializationFeature.INDENT_OUTPUT);
        return xmlMapper.writeValueAsString(target);
      }
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  public static <T> T fromXml(String content, Class<? extends T> type) {
    try {
      synchronized (xmlMapper) {
        return xmlMapper.readValue(content, type);
      }
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }


//  @JsonIgnoreProperties(ignoreUnknown = true)
//  @JacksonXmlRootElement(localName = "Request")
//  public class XmlRequest {
//
//    @JacksonXmlProperty(localName = "Header")
//    Header Header;
//
//    @JacksonXmlProperty(localName = "Body")
//    Body Body;
//  }

}
