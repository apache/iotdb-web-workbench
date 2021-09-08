package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DataDeleteDTO implements Serializable {

    @NotEmpty(message = "时间戳不能为空")
    private List<Date> timestampList;

    @NotEmpty(message = "物理量不能为空")
    private List<String> measurementList;
}
