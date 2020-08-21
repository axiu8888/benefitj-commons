package org.javastruct;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 基本数据类型
 */
public enum Primitive {
  BOOLEAN("boolean", 'Z', 0),
  BYTE("byte", 'B', 1),
  CHAR("char", 'C', 2),
  SHORT("short", 'S', 3),
  INT("int", 'I', 4),
  LONG("long", 'J', 5),
  FLOAT("float", 'F', 6),
  DOUBLE("double", 'D', 7),
  OBJECT("object", 'O', 8);

  final String type;
  final char signature;
  final int order;

  Primitive(String type, char signature, int order) {
    this.type = type;
    this.signature = signature;
    this.order = order;
  }



  static final Map<String, Primitive> PRIMITIVE_TYPES;
  static final Map<Character, Primitive> SIGNATURES;

  static {
    Map<String, Primitive> primitiveTypes = new HashMap<>();
    Map<Character, Primitive> signatures = new HashMap<>();
    for (Primitive p : Primitive.values()) {
      primitiveTypes.put(p.type, p);
      signatures.put(p.signature, p);
    }
    PRIMITIVE_TYPES = Collections.unmodifiableMap(primitiveTypes);
    SIGNATURES = Collections.unmodifiableMap(signatures);
  }

  public static Primitive getPrimitive(Field field) {
    if (!field.getType().isArray()) {
      return getPrimitive(field.getType().getName());
    } else {
      return getPrimitive(field.getType().getName().charAt(1));
    }
  }

  public static Primitive getPrimitive(String name) {
    return PRIMITIVE_TYPES.getOrDefault(name, Primitive.OBJECT);
  }

  public static Primitive getPrimitive(char signature) {
    return SIGNATURES.getOrDefault(signature, Primitive.OBJECT);
  }

}
