package org.apache.iotdb.admin.model.dto;

import lombok.Data;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @anthor fyx 2021/6/2
 */
@Data
public class Timeseries implements Serializable {
    private List<String> measurements = new ArrayList<>();
    /**
     *   六种type
     *      *   BOOLEAN
     *      *     INT32
     *      *     INT64
     *      *     FLOAT
     *      *     DOUBLE
     *      *     TEXT
     */
    private List<String> types = new ArrayList<>();
    private long time;
    private List<String> values;
}
