package com.benefitj.devices.breath;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.HexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class BreathTrainingTest {

  static Helper helper = Helper.get();

  @Test
  public void test_parse() {
    log.info("\n{}", JSON.toJSONString(
        data()
            .stream()
            .map(this::test)
            .collect(Collectors.toList()))
    );
  }

  @Test
  public void test_cmd() {
    log.info("exhale_train write cmd: {}", HexUtils.bytesToHex(helper.writeCmd(Cmd.exhale_train)));
    log.info("exhale_train read cmd: {}", HexUtils.bytesToHex(helper.readCmd(Cmd.exhale_train)));
  }

  @Test
  public void test_getCmd() {
    log.info("getCmd : {}", helper.getCmd(HexUtils.hexToBytes("7B0600640000C38F7D0D0A")));
    log.info("getCmd : {}", helper.getCmd(HexUtils.hexToBytes("7B0600640000C38F7D0D0A")));
    log.info("getCmd : {}", helper.getCmd(HexUtils.hexToBytes("7B060097000C33B97D0D0A")));
    log.info("getCmd : {}", helper.getCmd(HexUtils.hexToBytes("7B060064007A426C7D0D0A")));
    log.info("getCmd : {}", helper.getCmd(HexUtils.hexToBytes("7B030E00000000000CBBC1D1000001000060E67D0D0A")));
  }

  JSONObject test(String hex) {
    byte[] data = HexUtils.hexToBytes(hex);
    Map<Type, DataValue> map = helper.parse(data, Cmd.exhale_train.getType());
//    Map<Type, DataValue> map = helper.parse(data, Cmd.inhale_train.getType());
    log.info("\npayload =>: {}\nvalues =>: {}\n"
        , HexUtils.bytesToHex(helper.getPayload(data, true))
        , JSON.toJSONString(map)
    );
    JSONObject json = new JSONObject();
    json.put("payload", HexUtils.bytesToHex(helper.getPayload(data, true)));
    map.forEach((key, value) -> json.put(value.getDescriptor(), value.getValue() /*+ " _,_ " + value.getHex()*/));
    return json;
  }

  public List<String> slit(String data) {
    return Stream.of(data.split("\n"))
        .filter(StringUtils::isNotBlank)
//        .map(str -> str.split("data ==>: ")[1])
        .map(str -> str.split(",  data: ")[1])
        .map(String::trim)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());
  }

  public List<String> data() {
    return slit(
        "18:15:20.772 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0000000000203D6F984000,  data: 7B030E0000000000203D6F98400001000064317D0D0A \n" +
            "18:15:20.862 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0000000000203D62224000,  data: 7B030E0000000000203D62224000010000A26B7D0D0A \n" +
            "18:15:22.036 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0000000000203D29998000,  data: 7B030E0000000000203D2999800001000042357D0D0A \n" +
            "18:15:22.098 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0000000000203D4C98C000,  data: 7B030E0000000000203D4C98C000010000772D7D0D0A \n" +
            "18:15:22.217 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0000000000203D0BFC8000,  data: 7B030E0000000000203D0BFC800001000049C27D0D0A \n" +
            "18:15:23.012 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 000000010020C16DD3A500,  data: 7B030E000000010020C16DD3A5000200013FB17D0D0A \n" +
            "18:15:23.273 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 000000010020C1853B1100,  data: 7B030E000000010020C1853B1100020001F08D7D0D0A \n" +
            "18:15:23.785 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 000000020020C182D3EE00,  data: 7B030E000000020020C182D3EE00020002CA347D0D0A \n" +
            "18:15:24.200 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 000000020020C11DE29A00,  data: 7B030E000000020020C11DE29A0002000211177D0D0A \n" +
            "18:15:25.181 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0001000300203D47368000,  data: 7B030E0001000300203D4736800002000028697D0D0A \n" +
            "18:15:25.331 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0001000300203D14104000,  data: 7B030E0001000300203D141040000200004C7B7D0D0A \n" +
            "18:15:25.735 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 000100040020BCB70F0000,  data: 7B030E000100040020BCB70F00000200000F097D0D0A \n" +
            "18:15:26.220 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0001000400203D8EADE000,  data: 7B030E0001000400203D8EADE000020000F7327D0D0A \n" +
            "18:15:26.727 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0001000500203BAC4A0000,  data: 7B030E0001000500203BAC4A0000020000E7E77D0D0A \n" +
            "18:15:27.253 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0001000500203C62220000,  data: 7B030E0001000500203C6222000002000081257D0D0A \n" +
            "18:15:27.717 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0001000600203DA02D8000,  data: 7B030E0001000600203DA02D8000020000AD407D0D0A \n" +
            "18:15:28.292 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 0001000600203AAC500000,  data: 7B030E0001000600203AAC5000000200002B557D0D0A \n" +
            "18:15:28.726 测量数据(undefined) ==>: undefined,  undefined ,  吸气肌力训练,  吸气肌力训练,  payload: 000100070020BCA6E88000,  data: 7B030E000100070020BCA6E8800002000017457D0D0A \n"
    );
  }

}
