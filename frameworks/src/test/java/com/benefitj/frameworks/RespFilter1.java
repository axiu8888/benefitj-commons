package com.benefitj.frameworks;

import com.benefitj.core.SingletonSupplier;

import java.util.*;
import java.util.function.Function;

public class RespFilter1 {

  static final SingletonSupplier<Map<String, RespFilter1>[]> singleton
      = SingletonSupplier.of(() -> new Map[]{new HashMap<>(10), new HashMap(10)});
  static final Map<String, RespFilter1>[] WEAK_CACHE = new Map[]{new WeakHashMap(), new WeakHashMap()};
  static final Function<String, RespFilter1>[] CREATORS = new Function[]{
      (Function<String, RespFilter1>) deviceId -> {
        RespFilter1 filter = WEAK_CACHE[0].get(deviceId);
        return filter != null ? filter : new RespFilter1(deviceId);
      },
      (Function<String, RespFilter1>) deviceId -> {
        RespFilter1 filter = WEAK_CACHE[1].get(deviceId);
        return filter != null ? filter : new RespFilter1(deviceId);
      }
  };

  public static RespFilter1 get(int index, String deviceId) {
    return singleton.get()[index].computeIfAbsent(deviceId, CREATORS[index]);
  }

  public static void remove(String deviceId) {
    WEAK_CACHE[0].put(deviceId, singleton.get()[0].remove(deviceId));
    WEAK_CACHE[1].put(deviceId, singleton.get()[1].remove(deviceId));
  }


  String deviceId;

  List<Integer> res_buf = new LinkedList<>();
  List<Integer> new_buf = new LinkedList<>();
  float min_value;
  float max_value;
  int[] buf = new int[25];

  public RespFilter1(String deviceId) {
    this.deviceId = deviceId;
  }

  public int[] res_proc(int[] new_signal_value) {
    for (int i = 0; i < new_signal_value.length; i++) {
      buf[i] = res_proc(new_signal_value[i]);
    }
    return buf;
  }

  public int res_proc(int new_signal_value) {
    res_buf.add(new_signal_value);
    if (res_buf.size() < 25) {
      new_buf.add(new_signal_value);
      return new_signal_value;
    } else {
      res_buf.remove(0);
      new_buf.remove(0);
    }

    long window_sum = sum(res_buf);
    float mean_sum = (window_sum / 25f);
    new_buf.add((int) mean_sum);

    if (mean_sum < min_value) {
      min_value = mean_sum;
    } else if (mean_sum > max_value + 100) {
      max_value = mean_sum;
    }
    int v = (int) (((mean_sum - min_value) / (max_value - min_value)) * 1024);
    return v;
  }

  private long sum(List<Integer> list) {
    long sum = 0;
    for (Integer v : list) {
      sum += v;
    }
    return sum;
  }
}

//res_buf = deque(maxlen=200)
//de_tr_buf = deque(maxlen=200)
//min_value = float('inf')
//max_value = float('-inf')
//mean_buff = 0
//
//
//def res_proc(new_signal_value):
//    # new_signal_value = 1 / (1+np.exp(-new_signal_value/10000))
//    # return new_signal_value
//
//    global min_value, max_value, last, mean_buff
//    # mean_buff = mean_buff +
//    res_buf.append(new_signal_value)
//    if len(res_buf) < 200:
//        return 0, 0, 0
//    window_sum = sum(res_buf)
//    mean_sum = window_sum/200
//    de_tr = new_signal_value-mean_sum
//
//    de_tr_buf.append(de_tr)
//    temp = abs(int(max(de_tr_buf)/2.5))
//
//    y = 1 / (1 + np.exp(-de_tr/temp))
//    return y*1024
