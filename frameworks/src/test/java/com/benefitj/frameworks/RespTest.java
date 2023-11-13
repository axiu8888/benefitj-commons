package com.benefitj.frameworks;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class RespTest extends BaseTest {

  @Override
  public void setUp() {
  }

  @Override
  public void tearDown() {
  }

  @Test
  public void test() {
    RespFilter filter = new RespFilter();
  }

  static class RespFilter {
    List<Integer> res_buf = new LinkedList<>();
    List<Integer> new_buf = new LinkedList<>();
    int min_value;
    int max_value;
    int last;
    int[] buf = new int[25];

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
      int mean_sum = (int) (window_sum/25);
      new_buf.add(mean_sum);

      if (mean_sum < min_value) {
        min_value = mean_sum;
      } else if(mean_sum > max_value+100) {
        max_value = mean_sum;
      }
      return (mean_sum - min_value) / (max_value - min_value);
    }

    private long sum(List<Integer> list) {
      long sum = 0;
      for (Integer v : list) {
        sum += v;
      }
      return sum;
    }

  }

//res_buf = deque(maxlen=25)
//new_buf = deque(maxlen=25)
//min_value = float('inf')
//max_value = float('-inf')
//
//
//def res_proc(new_signal_value):
//    global min_value, max_value, last
//    res_buf.append(new_signal_value)
//    if len(res_buf) < 25:
//        new_buf.append(new_signal_value)
//        return 0
//
//    window_sum = sum(res_buf)
//    mean_sum = window_sum/25
//    new_buf.append(mean_sum)
//
//    if mean_sum < min_value:
//        min_value = mean_sum
//    if mean_sum > max_value+100:
//        max_value = mean_sum
//    scaled_value = (mean_sum - min_value) / (max_value - min_value)
//
//    return scaled_value

}
