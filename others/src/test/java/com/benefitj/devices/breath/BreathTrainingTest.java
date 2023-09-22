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

  static BreathTrainingHelper helper = BreathTrainingHelper.get();

  @Test
  public void test_parse() {
    log.info("\n{}", JSON.toJSONString(
//        data1()
//        data2()
//        data3()
        data4()
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

  JSONObject test(String hex) {
    byte[] data = HexUtils.hexToBytes(hex);
    Map<Type, DataValue> map = helper.parse(data, Type.EXHALE_TRAIN_TIMES);
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
        .map(str -> str.split("data ==>: ")[1])
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());
  }

  public List<String> data1() {
    return slit(
        "2023-09-21 18:19:29.632 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4136401A00020000D8AD7D0D0A\n" +
            "2023-09-21 18:19:29.843 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413D568C0002000028867D0D0A\n" +
            "2023-09-21 18:19:29.918 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413D0DDE000200009C617D0D0A\n" +
            "2023-09-21 18:19:30.002 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413DDD26000200002E257D0D0A\n" +
            "2023-09-21 18:19:30.170 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413A54A000020000CE627D0D0A\n" +
            "2023-09-21 18:19:30.249 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413909820002000008287D0D0A\n" +
            "2023-09-21 18:19:30.356 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4148234C0002000007357D0D0A\n" +
            "2023-09-21 18:19:30.740 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C413CBD1A00020001A9C47D0D0A\n" +
            "2023-09-21 18:19:31.255 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C414DB8CA0002000109447D0D0A\n" +
            "2023-09-21 18:19:31.796 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C413E94380002000231077D0D0A\n" +
            "2023-09-21 18:19:32.254 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C413B08FE00020002F18A7D0D0A\n" +
            "2023-09-21 18:19:32.769 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000004000C41459A800002000380817D0D0A\n" +
            "2023-09-21 18:19:33.230 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000004000C4120A69C0002000361797D0D0A\n" +
            "2023-09-21 18:19:34.217 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000CBE3B19200002000088147D0D0A\n" +
            "2023-09-21 18:19:34.325 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000CBE1ACB30000200006AA77D0D0A\n" +
            "2023-09-21 18:19:34.792 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000CBCA6E88000020000DF787D0D0A\n" +
            "2023-09-21 18:19:35.268 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000CBD345E8000020000B72B7D0D0A\n" +
            "2023-09-21 18:19:35.765 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010007000CBE28ED400002000034117D0D0A\n" +
            "2023-09-21 18:19:36.316 32454-521   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010007000CBCBC7200000200007C617D0D0A"
    );
  }


  public List<String> data2() {
    return slit(
        "2023-09-21 18:30:08.123 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C40DC9F7100020001A5C97D0D0A\n" +
            "2023-09-21 18:30:08.243 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C415642BC0002000170907D0D0A\n" +
            "2023-09-21 18:30:08.399 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4143044600020001E2E37D0D0A\n" +
            "2023-09-21 18:30:08.536 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C414477C6000200019EFE7D0D0A\n" +
            "2023-09-21 18:30:08.578 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4149286E000200012F497D0D0A\n" +
            "2023-09-21 18:30:08.699 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413CC7DE000200015CCB7D0D0A\n" +
            "2023-09-21 18:30:08.860 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4142BE480002000181787D0D0A\n" +
            "2023-09-21 18:30:08.939 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413FBC5600020001859F7D0D0A\n" +
            "2023-09-21 18:30:09.058 32454-32699 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413E462600020001C0CE7D0D0A\n" +
            "2023-09-21 18:30:09.380 32454-32699 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C413F1AD20002000222027D0D0A\n" +
            "2023-09-21 18:30:09.856 32454-32699 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C41453EFA000200029F277D0D0A\n" +
            "2023-09-21 18:30:10.326 32454-32699 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C413A472C0002000396A77D0D0A\n" +
            "2023-09-21 18:30:10.859 32454-32699 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C411F60E000020003F4A27D0D0A\n" +
            "2023-09-21 18:30:11.937 32454-32699 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3DD353E000020000A2E87D0D0A\n" +
            "2023-09-21 18:30:12.044 32454-32699 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000CBC370E000002000043D17D0D0A\n" +
            "2023-09-21 18:30:12.464 32454-4748  [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3BD7600000020000E7EB7D0D0A\n" +
            "2023-09-21 18:30:12.877 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3C915F800002000085187D0D0A\n" +
            "2023-09-21 18:30:13.356 32454-32619 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000C3DBDCA8000020000BB577D0D0A\n" +
            "2023-09-21 18:30:13.842 32454-32619 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000CBCA1860000020000A0E87D0D0A\n" +
            "2023-09-21 18:30:14.358 32454-4748  [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010007000C3C0BFD0000020000AC357D0D0A\n" +
            "2023-09-21 18:30:14.868 32454-4748  [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010007000CBBAC480000020000291C7D0D0A\n" +
            "2023-09-21 18:30:15.416 32454-32619 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010008000C3DEF984000020000F1D97D0D0A\n" +
            "2023-09-21 18:30:15.883 32454-419   [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010008000C3E4531A00002000022297D0D0A\n" +
            "2023-09-21 18:30:16.400 32454-4748  [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010009000CBB013000000200006B467D0D0A\n" +
            "2023-09-21 18:30:16.400 32454-4748  [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010009000CBB013000000200006B467D0D0A\n" +
            "2023-09-21 18:30:16.878 32454-4748  [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010009000C3D74FA8000020000D7BF7D0D0A\n" +
            "2023-09-21 18:30:17.380 32454-32701 [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000A000C3D7A5D00000200002FF27D0D0A\n" +
            "2023-09-21 18:30:17.995 32454-4748  [(Binder:3...tViewModel com.hsrg.android.example             I  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000A000C3DA590000002000011227D0D0A"
    );
  }

  public List<String> data3() {
    return slit(
        "2023-09-22 11:33:17.517 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4149082400020001B1E77D0D0A\n" +
            "2023-09-22 11:33:17.677 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4147B7A000020001B4F67D0D0A\n" +
            "2023-09-22 11:33:17.823 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C41424A8400020001841C7D0D0A\n" +
            "2023-09-22 11:33:18.027 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413C10D400020001D6ED7D0D0A\n" +
            "2023-09-22 11:33:18.110 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C41486BFC0002000188A77D0D0A\n" +
            "2023-09-22 11:33:18.263 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4133131C00020001C80E7D0D0A\n" +
            "2023-09-22 11:33:18.364 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C414BF1D600020001BF9B7D0D0A\n" +
            "2023-09-22 11:33:18.523 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C414395A60002000170F07D0D0A\n" +
            "2023-09-22 11:33:18.646 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C4144372A000200024EAC7D0D0A\n" +
            "2023-09-22 11:33:18.703 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C4143D8F2000200020EE07D0D0A\n" +
            "2023-09-22 11:33:18.814 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C41434A4400020002DB887D0D0A\n" +
            "2023-09-22 11:33:19.024 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C413E73EC00020002123E7D0D0A\n" +
            "2023-09-22 11:33:19.142 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C4142E14400020002D2937D0D0A\n" +
            "2023-09-22 11:33:19.290 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C414C8DFC0002000294247D0D0A\n" +
            "2023-09-22 11:33:19.402 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C41472BA40002000217EF7D0D0A\n" +
            "2023-09-22 11:33:19.546 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C4137B8FE00020003E67A7D0D0A\n" +
            "2023-09-22 11:33:19.682 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C413875700002000361787D0D0A\n" +
            "2023-09-22 11:33:19.875 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C40D7FEED000200034D1D7D0D0A" +
            "2023-09-21 19:29:13.946 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C411B61E300020000BF0A7D0D0A\n" +
            "2023-09-21 19:29:14.309 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C414684B9000200007DF77D0D0A\n" +
            "2023-09-21 19:29:14.329 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4147E81400020000C9837D0D0A\n" +
            "2023-09-21 19:29:14.387 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4144142900020000837A7D0D0A\n" +
            "2023-09-21 19:29:14.397 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4141B3C400020000637A7D0D0A\n" +
            "2023-09-21 19:29:14.457 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C414295E500020000EB7B7D0D0A\n" +
            "2023-09-21 19:29:14.489 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C414C7DD100020000A2577D0D0A\n" +
            "2023-09-21 19:29:14.554 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4148209A000200004ED47D0D0A\n" +
            "2023-09-21 19:29:14.648 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C413FE4BA0002000116147D0D0A\n" +
            "2023-09-21 19:29:14.859 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C4148030100020001E2DF7D0D0A\n" +
            "2023-09-21 19:29:14.984 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C413B294C000200018BDC7D0D0A\n" +
            "2023-09-21 19:29:15.114 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C4143B33F000200016BEA7D0D0A\n" +
            "2023-09-21 19:29:15.258 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C413857D500020001AEDE7D0D0A\n" +
            "2023-09-21 19:29:15.407 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C41437AB7000200019A6C7D0D0A\n" +
            "2023-09-21 19:29:16.190 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010003000CBDE7846000020000D6C67D0D0A\n" +
            "2023-09-21 19:29:16.296 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010003000C3DB45E40000200002F5E7D0D0A\n" +
            "2023-09-21 19:29:16.396 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010003000C3DF24920000200008BB57D0D0A\n" +
            "2023-09-21 19:29:16.515 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010003000C3E049550000200000F387D0D0A\n" +
            "2023-09-21 19:29:16.647 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3E1615100002000039427D0D0A\n" +
            "2023-09-21 19:29:16.782 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3DF0F0A000020000A9767D0D0A\n" +
            "2023-09-21 19:29:16.876 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3E223250000200004B997D0D0A\n" +
            "2023-09-21 19:29:16.966 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3E197280000200000E487D0D0A\n" +
            "2023-09-21 19:29:17.079 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3D9D7C40000200008B6A7D0D0A\n" +
            "2023-09-21 19:29:17.200 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3E120B4000020000BF307D0D0A\n" +
            "2023-09-21 19:29:17.281 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3E1A1EE000020000B42C7D0D0A\n" +
            "2023-09-21 19:29:17.461 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3DF9040000020000A51B7D0D0A\n" +
            "2023-09-21 19:29:17.508 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3E258FD00002000027AA7D0D0A\n" +
            "2023-09-21 19:29:17.679 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3D39C0400002000070A07D0D0A\n" +
            "2023-09-21 19:29:17.729 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3E24E3A0000200007B317D0D0A\n" +
            "2023-09-21 19:29:17.838 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3DF7AB6000020000D6707D0D0A\n" +
            "2023-09-21 19:29:17.998 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3E01E450000200004E517D0D0A\n" +
            "2023-09-21 19:29:18.067 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3E069A5000020000328F7D0D0A\n" +
            "2023-09-21 19:29:18.179 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3E008BC00002000097D37D0D0A\n" +
            "2023-09-21 19:29:18.328 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3E1E289000020000B14D7D0D0A\n" +
            "2023-09-21 19:29:18.398 10047-10261 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3E16C14000020000EFA67D0D0A\n" +
            "2023-09-21 19:29:18.509 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3E24373000020000A9387D0D0A\n" +
            "2023-09-21 19:29:18.629 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000C3DFBB54000020000974D7D0D0A\n" +
            "2023-09-21 19:29:18.758 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000C3DA4374000020000266A7D0D0A\n" +
            "2023-09-21 19:29:18.856 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000C3DFE664000020000D1EE7D0D0A\n" +
            "2023-09-21 19:29:18.969 10047-10295 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000C3DD0A260000200008EAF7D0D0A\n"
    );
  }
  public List<String> data4() {
    return slit(
        "2023-09-22 11:33:17.517 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4149082400020001B1E77D0D0A\n" +
            "2023-09-22 11:33:17.677 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4147B7A000020001B4F67D0D0A\n" +
            "2023-09-22 11:33:17.823 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C41424A8400020001841C7D0D0A\n" +
            "2023-09-22 11:33:18.027 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C413C10D400020001D6ED7D0D0A\n" +
            "2023-09-22 11:33:18.110 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C41486BFC0002000188A77D0D0A\n" +
            "2023-09-22 11:33:18.263 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C4133131C00020001C80E7D0D0A\n" +
            "2023-09-22 11:33:18.364 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000001000C414BF1D600020001BF9B7D0D0A\n" +
            "2023-09-22 11:33:18.523 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C414395A60002000170F07D0D0A\n" +
            "2023-09-22 11:33:18.646 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C4144372A000200024EAC7D0D0A\n" +
            "2023-09-22 11:33:18.703 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C4143D8F2000200020EE07D0D0A\n" +
            "2023-09-22 11:33:18.814 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C41434A4400020002DB887D0D0A\n" +
            "2023-09-22 11:33:19.024 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C413E73EC00020002123E7D0D0A\n" +
            "2023-09-22 11:33:19.142 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C4142E14400020002D2937D0D0A\n" +
            "2023-09-22 11:33:19.290 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C414C8DFC0002000294247D0D0A\n" +
            "2023-09-22 11:33:19.402 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000002000C41472BA40002000217EF7D0D0A\n" +
            "2023-09-22 11:33:19.546 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C4137B8FE00020003E67A7D0D0A\n" +
            "2023-09-22 11:33:19.682 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C413875700002000361787D0D0A\n" +
            "2023-09-22 11:33:19.875 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00000003000C40D7FEED000200034D1D7D0D0A\n" +
            "023-09-22 11:33:20.501 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3D869AC00002000037B37D0D0A\n" +
            "2023-09-22 11:33:20.596 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000CBDC9E740000200008AE47D0D0A\n" +
            "2023-09-22 11:33:20.726 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000CBAAC400000020000E6DC7D0D0A\n" +
            "2023-09-22 11:33:20.884 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3DCDF1600002000044757D0D0A\n" +
            "2023-09-22 11:33:20.942 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000CBB575000000200006F4F7D0D0A\n" +
            "2023-09-22 11:33:21.049 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000CBE24E37000020000B67F7D0D0A\n" +
            "2023-09-22 11:33:21.214 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3DA8418000020000EA957D0D0A\n" +
            "2023-09-22 11:33:21.257 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000C3E350AB000020000A9F67D0D0A\n" +
            "2023-09-22 11:33:21.363 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010004000CBD03E90000020000E0097D0D0A\n" +
            "2023-09-22 11:33:21.486 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000CBE24370000020000E15C7D0D0A\n" +
            "2023-09-22 11:33:21.589 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000CBDFA5C0000020000979F7D0D0A\n" +
            "2023-09-22 11:33:21.718 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3D26E8C00002000099977D0D0A\n" +
            "2023-09-22 11:33:21.887 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000CBCF2468000020000DC777D0D0A\n" +
            "2023-09-22 11:33:21.987 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000CBD1C2340000200001A517D0D0A\n" +
            "2023-09-22 11:33:22.038 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3C0C000000020000C4A07D0D0A\n" +
            "2023-09-22 11:33:22.173 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3E03E94000020000AD4F7D0D0A\n" +
            "2023-09-22 11:33:22.264 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000C3D0EAE40000200003EED7D0D0A\n" +
            "2023-09-22 11:33:22.393 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010005000CBD92B74000020000688D7D0D0A\n" +
            "2023-09-22 11:33:22.500 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000CBD1ED40000020000225D7D0D0A\n" +
            "2023-09-22 11:33:22.609 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000CBDD75D000002000064087D0D0A\n" +
            "2023-09-22 11:33:22.711 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000C3DA2DEC000020000574D7D0D0A\n" +
            "2023-09-22 11:33:22.819 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000CBD622200000200008B0C7D0D0A\n" +
            "2023-09-22 11:33:22.921 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000C3D6CE780000200007C277D0D0A\n" +
            "2023-09-22 11:33:23.073 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000C3E03E9600002000023CC7D0D0A\n" +
            "2023-09-22 11:33:23.326 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010006000CBCF7AB8000020000914E7D0D0A\n" +
            "2023-09-22 11:33:23.521 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010007000CBD51FA80000200009FF57D0D0A\n" +
            "2023-09-22 11:33:23.701 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010007000CBCBC6F80000200007E327D0D0A\n" +
            "2023-09-22 11:33:23.868 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010007000C3D2EFCC00002000018FB7D0D0A\n" +
            "2023-09-22 11:33:24.107 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010007000C3B2C500000020000A36C7D0D0A\n" +
            "2023-09-22 11:33:24.289 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010007000C3DCF4A600002000073BA7D0D0A\n" +
            "2023-09-22 11:33:24.517 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010008000CBB2C40000002000099887D0D0A\n" +
            "2023-09-22 11:33:24.700 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010008000CBC2C470000020000D9D97D0D0A\n" +
            "2023-09-22 11:33:24.893 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010008000CBCE7838000020000B24F7D0D0A\n" +
            "2023-09-22 11:33:25.084 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010008000CBE0EADC0000200004CD97D0D0A\n" +
            "2023-09-22 11:33:25.312 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010008000CBD9C238000020000A0247D0D0A\n" +
            "2023-09-22 11:33:25.488 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010008000CBE0CA900000200006E8C7D0D0A\n" +
            "2023-09-22 11:33:25.745 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010009000CBCA6E68000020000EE427D0D0A\n" +
            "2023-09-22 11:33:25.886 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010009000CBC013500000200002AF57D0D0A\n" +
            "2023-09-22 11:33:26.069 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010009000C3D1C2400000200002D197D0D0A\n" +
            "2023-09-22 11:33:26.328 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E00010009000CBC622000000200007B367D0D0A\n" +
            "2023-09-22 11:33:26.550 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000A000C3D85422000020000A3557D0D0A\n" +
            "2023-09-22 11:33:26.699 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000A000CBDA9998000020000D4057D0D0A\n" +
            "2023-09-22 11:33:26.885 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000A000C3DCDF18000020000F14B7D0D0A\n" +
            "2023-09-22 11:33:27.095 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000A000CBE0B50700002000046567D0D0A\n" +
            "2023-09-22 11:33:27.300 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000A000CBDD0A2800002000038497D0D0A\n" +
            "2023-09-22 11:33:27.496 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000A000C3DB1AD6000020000C1C67D0D0A\n" +
            "2023-09-22 11:33:27.698 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000B000CBDBB18A0000200007D2E7D0D0A\n" +
            "2023-09-22 11:33:27.874 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000B000C3D77AC4000020000E3207D0D0A\n" +
            "2023-09-22 11:33:28.099 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000B000CBD26E74000020000A43E7D0D0A\n" +
            "2023-09-22 11:33:28.295 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000B000C3D0BFD800002000053777D0D0A\n" +
            "2023-09-22 11:33:28.508 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000B000C3E008BE00002000022FC7D0D0A\n" +
            "2023-09-22 11:33:28.708 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000C000CBD115E8000020000E2317D0D0A\n" +
            "2023-09-22 11:33:28.927 18482-18737 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000C000C3DE0CA000002000017947D0D0A\n" +
            "2023-09-22 11:33:29.112 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000C000C3CBC72000002000051257D0D0A\n" +
            "2023-09-22 11:33:29.339 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000C000CBD243680000200008FDA7D0D0A\n" +
            "2023-09-22 11:33:29.529 18482-18506 [(Binder:1...tViewModel com.hsrg.android.example             D  onCharacteristicChanged, 38:3B:26:1A:27:5B, data ==>: 7B030E0001000D000C3B014400000200007CA27D0D0A"
    );
  }

}
