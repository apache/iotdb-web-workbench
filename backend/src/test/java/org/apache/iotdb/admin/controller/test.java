package org.apache.iotdb.admin.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** @author fanli */
public class test {

  public List<Integer> sort(List<Integer> list) {
    System.out.println("排序前：" + list);
    Collections.sort(
        list,
        new Comparator<Integer>() {
          @Override
          public int compare(Integer lhs, Integer rhs) {
            if (lhs > rhs) {
              return 1;
            } else {
              return -1;
            }
          }
        });
    System.out.println("排序后：" + list);
    return null;
  }
}
