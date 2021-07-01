package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @anthor fyx 2021/7/1
 */
@Data
public class RecordVO implements Serializable {
    private List<Date> timeList;
    private List<Long> valueList;
}
