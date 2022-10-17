package org.apache.iotdb.admin.common;

public enum AggregateFunctionEnum {
  SUM(" sum"),
  COUNT("count"),
  AVG(" avg"),
  EXTREME(" extreme"),
  MAX_VALUE(" max_value"),
  MIN_VALUE(" min_value"),
  FIRST_VALUE(" first_value"),
  LAST_VALUE(" last_value"),
  MAX_TIME(" max_time"),
  MIN_TIME(" min_time");

  private String name;

  AggregateFunctionEnum(String name) {
    this.name = name;
  }

  // 聚合查询
  public static boolean isAggregateFunctionQuery(String sql) {
    boolean isAggregate = false;
    if (null == sql) {
      return false;
    }
    sql = sql.toLowerCase();
    AggregateFunctionEnum[] values = AggregateFunctionEnum.values();
    for (AggregateFunctionEnum functionEnum : values) {
      if (sql.indexOf(functionEnum.name) != -1) {
        isAggregate = true;
        break;
      }
    }
    return isAggregate;
  }
}
