package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/6/22
 */
@Data
public class DeviceDTO implements Serializable {

    private List<String> measurements;

    private List<String> types;

    private List<String> encoding;

    @NotNull(message = "存储组名不能为null")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String deviceName;

    private String description;

    private List<String> descriptions;

}
