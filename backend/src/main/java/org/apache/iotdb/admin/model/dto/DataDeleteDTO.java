package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DataDeleteDTO implements Serializable {

    @NotNull(message = "时间戳不能为null")
    private List<Date> timestampList;

    private List<String> measurementList;
}
