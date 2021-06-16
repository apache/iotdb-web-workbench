package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 传输时间序列信息的类
 */
@Data
public class Timeseries implements Serializable {

    private List<String> measurements = new ArrayList<>();

    private List<String> types = new ArrayList<>();

    private long time;

    private List<String> values;

}
