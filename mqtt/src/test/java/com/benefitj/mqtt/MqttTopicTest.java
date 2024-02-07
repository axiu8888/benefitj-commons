package com.benefitj.mqtt;

import com.benefitj.core.Slicer;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class MqttTopicTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testTopicFilter() {

    // /event/#
    // /event/#/
    // /event/+
    // /event/+/
    // /event/+/msg
    // event/+/msg
    // #/event/+/msg =>:  0112/event/bind/msg  &&  0112/person/event/bind/msg
    // #/+/msg =>:  0112/event/bind/msg  &&  0112/person/event/bind/msg

    long start = System.currentTimeMillis();
    System.err.println("1. true #/event/+/msg  &&  0112/event/bind/msg ==>: " + (MqttTopic.get("#/event/+/msg").match("0112/event/bind/msg")));
    System.err.println("2. true #/event/+/msg  &&  0112/event/bind/msg ==>: " + (MqttTopic.get("#/event/+/msg").match("0112/person/event/bind/msg")));
    System.err.println("3. #/event/+/msg  &&  0112//event/bind/msg ==>: " + (MqttTopic.get("#/event/+/msg").match("0112//event/bind/msg")));
    System.err.println("4. #/event/+/msg  &&  /0112/event/bind/msg ==>: " + (MqttTopic.get("#/event/+/msg").match("/0112/event/bind/msg")));
    System.err.println("5. /#/event/+/msg  &&  /0112/event/bind/msg ==>: " + (MqttTopic.get("/#/event/+/msg").match("/0112/event/bind/msg")));
    System.err.println("6. /event/#/event/+/msg  &&  /0112/event/bind/msg ==>: " + (MqttTopic.get("/event/#/event/+/msg").match("/0112/event/bind/msg")));
    System.err.println("7. #/+/msg  &&  0112/event/bind/msg ==>: " + (MqttTopic.get("#/+/msg").match("0112/event/bind/msg")));
    System.err.println("8. #/+/msg  &&  0112/person/event/bind/msg ==>: " + (MqttTopic.get("#/+/msg").match("0112/person/event/bind/msg")));
    System.err.println("9. /#/+/msg  &&  0112/person/event/bind/msg ==>: " + (MqttTopic.get("/#/+/msg").match("0112/person/event/bind/msg")));
    System.err.println("10. #  &&  0112/person/event/bind/msg ==>: " + (MqttTopic.get("#").match("0112/person/event/bind/msg")));
    System.err.println("11. +  &&  0112/person/event/bind/msg ==>: " + (MqttTopic.get("+").match("0112/person/event/bind/msg")));
    System.err.println("12. person/+  &&  person/0112 ==>: " + (MqttTopic.get("person/+").match("person/0112")));
    System.err.println("13. person/+/+  &&  person/0112/22222 ==>: " + (MqttTopic.get("person/+/+").match("person/0112/22222")));
    System.err.println("14. person/+/+  &&  person/0112/22222/ssss ==>: " + (MqttTopic.get("person/+/+").match("person/0112/22222/ssss")));
    System.err.println("15. person/+/#  &&  person/0112/22222/ssss ==>: " + (MqttTopic.get("person/+/#").match("person/0112/22222/ssss")));
    System.err.println("16.  &&  person ==>: " + (MqttTopic.get("").match("person")));
    System.err.println("17. +  &&  person ==>: " + (MqttTopic.get("+").match("person")));
    System.err.println("18. +/  &&  person ==>: " + (MqttTopic.get("+/").match("person")));
    System.err.println("19. +/  &&  person/ ==>: " + (MqttTopic.get("+/").match("person/")));
    System.err.println("20. +/  &&  person/aabb ==>: " + (MqttTopic.get("+/").match("person/aabb")));
    System.err.println("21. +/+  &&  person/ ==>: " + (MqttTopic.get("+/+").match("person")));

    System.err.println("22. event/+/+/+  &&  event/collector/bindSpo2/01000384 ==>: " + (MqttTopic.get("event/+/+/+").match("event/collector/bindSpo2/01000384")));
    System.err.println("23. event/+/+/+/+  &&  event/collector/bindSpo2/01000384 ==>: " + (MqttTopic.get("event/+/+/+/+").match("event/collector/bindSpo2/01000384")));

    System.err.println("spend: " + (System.currentTimeMillis() - start));
  }

  @Test
  public void testSlicer() {
    System.err.println(Slicer.slice("event/collector/bindSpo2/01000384/", ((b, position, ch) -> ch == '/')));
    System.err.println(Slicer.slice("/event/collector/bindSpo2/01000384/", ((b, position, ch) -> ch == '/')));
    System.err.println(Slicer.slice("//", ((b, position, ch) -> ch == '/')));
  }

  @AfterEach
  public void tearDown() throws Exception {
  }
}