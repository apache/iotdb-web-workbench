package org.apache.iotdb.admin.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @anthor fyx 2021/6/16
 */
@Data
public class Measurement implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "未指定所属组")
    private Integer deviceId;

    private String description;
}
