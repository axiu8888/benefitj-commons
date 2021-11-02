package com.benefitj.mqtt;

import com.benefitj.core.Slicer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
    System.err.println("true #/event/+/msg  &&  0112/event/bind/msg ==>: " + (MqttTopic.get("#/event/+/msg").match("0112/event/bind/msg")));
    System.err.println("true #/event/+/msg  &&  0112/event/bind/msg ==>: " + (MqttTopic.get("#/event/+/msg").match("0112/person/event/bind/msg")));
    System.err.println("#/event/+/msg  &&  0112//event/bind/msg ==>: " + (MqttTopic.get("#/event/+/msg").match("0112//event/bind/msg")));
    System.err.println("#/event/+/msg  &&  /0112/event/bind/msg ==>: " + (MqttTopic.get("#/event/+/msg").match("/0112/event/bind/msg")));
    System.err.println("/#/event/+/msg  &&  /0112/event/bind/msg ==>: " + (MqttTopic.get("/#/event/+/msg").match("/0112/event/bind/msg")));
    System.err.println("/event/#/event/+/msg  &&  /0112/event/bind/msg ==>: " + (MqttTopic.get("/event/#/event/+/msg").match("/0112/event/bind/msg")));
    System.err.println("#/+/msg  &&  0112/event/bind/msg ==>: " + (MqttTopic.get("#/+/msg").match("0112/event/bind/msg")));
    System.err.println("#/+/msg  &&  0112/person/event/bind/msg ==>: " + (MqttTopic.get("#/+/msg").match("0112/person/event/bind/msg")));
    System.err.println("/#/+/msg  &&  0112/person/event/bind/msg ==>: " + (MqttTopic.get("/#/+/msg").match("0112/person/event/bind/msg")));
    System.err.println("#  &&  0112/person/event/bind/msg ==>: " + (MqttTopic.get("#").match("0112/person/event/bind/msg")));
    System.err.println("+  &&  0112/person/event/bind/msg ==>: " + (MqttTopic.get("+").match("0112/person/event/bind/msg")));
    System.err.println("person/+  &&  person/0112 ==>: " + (MqttTopic.get("person/+").match("person/0112")));
    System.err.println("person/+/+  &&  person/0112/22222 ==>: " + (MqttTopic.get("person/+/+").match("person/0112/22222")));
    System.err.println("person/+/+  &&  person/0112/22222/ssss ==>: " + (MqttTopic.get("person/+/+").match("person/0112/22222/ssss")));
    System.err.println("person/+/#  &&  person/0112/22222/ssss ==>: " + (MqttTopic.get("person/+/#").match("person/0112/22222/ssss")));
    System.err.println("  &&  person ==>: " + (MqttTopic.get("").match("person")));
    System.err.println("+  &&  person ==>: " + (MqttTopic.get("+").match("person")));
    System.err.println("+/  &&  person ==>: " + (MqttTopic.get("+/").match("person")));
    System.err.println("+/  &&  person/ ==>: " + (MqttTopic.get("+/").match("person/")));
    System.err.println("+/  &&  person/aabb ==>: " + (MqttTopic.get("+/").match("person/aabb")));
    System.err.println("+/+  &&  person/ ==>: " + (MqttTopic.get("+/+").match("person")));

    System.err.println("event/+/+/+  &&  event/collector/bindSpo2/01000384 ==>: " + (MqttTopic.get("event/+/+/+").match("event/collector/bindSpo2/01000384")));
    System.err.println("event/+/+/+/+  &&  event/collector/bindSpo2/01000384 ==>: " + (MqttTopic.get("event/+/+/+/+").match("event/collector/bindSpo2/01000384")));

    System.err.println("spend: " + (System.currentTimeMillis() - start));
  }

  @Test
  public void testSlicer() {
    System.err.println(Slicer.slice("event/collector/bindSpo2/01000384/", ((b, position, ch) -> ch == '/')));
    System.err.println(Slicer.slice("/event/collector/bindSpo2/01000384/", ((b, position, ch) -> ch == '/')));
    System.err.println(Slicer.slice("//", ((b, position, ch) -> ch == '/')));
  }

  @After
  public void tearDown() throws Exception {
  }
}