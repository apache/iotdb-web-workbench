package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DataUpdateDTO implements Serializable {

    @NotNull(message = "时间戳不能为null")
    private Date timestamp;

    @NotEmpty(message = "值列表不能为空")
    private List<String> valueList;

    @NotEmpty(message = "物理量列表不能为空")
    private List<String> measurementList;
}
