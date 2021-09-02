package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DataQueryDTO implements Serializable {

    private Date startTime;

    private Date endTime;

    private List<String> measurementList;
}
