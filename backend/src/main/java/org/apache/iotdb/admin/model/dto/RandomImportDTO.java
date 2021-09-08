package org.apache.iotdb.admin.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class RandomImportDTO implements Serializable {

    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    @NotNull(message = "步长不能为空")
    @Min(value = 1, message = "步长至少为1ms")
    private Integer stepSize;

    @NotNull(message = "数据行数不能为空")
    @Range(min = 1, max = 1000000, message = "数据行数在1到1000000之间")
    private Integer totalLine;
}
