package org.apache.iotdb.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class Role implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "未指定角色名")
    @Length(min = 4, message = "角色名长度至少为4")
    private String name;

    @NotNull(message = "未指定所属主机")
    private String host;

    @NotNull(message = "未指定所属主机的端口号")
    private Integer port;

    private String description;
}
