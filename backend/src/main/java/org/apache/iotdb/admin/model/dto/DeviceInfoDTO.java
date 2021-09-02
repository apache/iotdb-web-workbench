package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
public class DeviceInfoDTO implements Serializable {

    private List<DeviceDTO> deviceDTOList;

    // TODO: 现在设备名可以为空了
    @NotNull(message = "设备名不能为null")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String deviceName;

    private String description;

    private Integer deviceId;
}
