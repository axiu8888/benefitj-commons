package com.benefitj.frameworks;

import com.benefitj.core.EventLoop;
import com.benefitj.core.SingletonSupplier;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class RespFilter2 {

  static final SingletonSupplier<Map<String, RespFilter2>[]> singleton
      = SingletonSupplier.of(() -> new Map[]{new HashMap<>(10), new HashMap(10)});
  static final Map<String, RespFilter2>[] WEAK_CACHE = new Map[]{new WeakHashMap(), new WeakHashMap()};
  static final Function<String, RespFilter2>[] CREATORS = new Function[]{
      (Function<String, RespFilter2>) deviceId -> {
        RespFilter2 filter = WEAK_CACHE[0].get(deviceId);
        return filter != null ? filter : new RespFilter2(deviceId);
      },
      (Function<String, RespFilter2>) deviceId -> {
        RespFilter2 filter = WEAK_CACHE[1].get(deviceId);
        return filter != null ? filter : new RespFilter2(deviceId);
      }
  };

  public static RespFilter2 get(int index, String deviceId) {
    return singleton.get()[index].computeIfAbsent(deviceId, CREATORS[index]);
  }

  public static void remove(String deviceId) {
    WEAK_CACHE[0].put(deviceId, singleton.get()[0].remove(deviceId));
    WEAK_CACHE[1].put(deviceId, singleton.get()[1].remove(deviceId));
  }


  static final EventLoop loop = EventLoop.newSingle(true);

  String deviceId;

  List<Integer> res_buf = new LinkedList<>();
  List<Double> de_tr_buf = new LinkedList<>();

  int[] buf = new int[25];

  public RespFilter2(String deviceId) {
    this.deviceId = deviceId;
  }

  public int[] res_proc(int[] new_signal_value) {
    CountDownLatch latch = new CountDownLatch(1);
    loop.execute(() -> {
      for (int i = 0; i < new_signal_value.length; i++) {
        buf[i] = res_proc(new_signal_value[i]);
      }
      latch.countDown();
    });
    try {
      latch.await(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
    }
    return buf;
  }

  public int res_proc(int new_signal_value) {
    res_buf.add(new_signal_value);
    if (res_buf.size() < 200) {
      de_tr_buf.add((double) new_signal_value);
      return 0;
    } else {
      res_buf.remove(0);
      de_tr_buf.remove(0);
    }
    long window_sum = sum(res_buf);
    float mean_sum = window_sum / 200f;
    double de_tr = new_signal_value - mean_sum;

    de_tr_buf.add(de_tr);
    double temp = Math.abs(max(de_tr_buf) / 2.5);
    double y = (1 / (1 + Math.exp(-de_tr / temp)));
    return (int) (y * 1024);
  }

  private double max(List<Double> list) {
    double max = 0;
    for (Double v : list) {
      max = Math.max(v, max);
    }
    return max;
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
//
//
//def res_proc(new_signal_value):
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
