package org.apache.iotdb.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @anthor fyx 2021/6/16
 */
@Data
public class Device implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "未指定所属主机")
    private String host;

    private String description;

    @NotBlank
    @Length(min = 4,message = "创建者名长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$",message = "创建者名不能包含空格")
    private String creator;

    @NotNull(message = "未指定创建时间")
    private Long createTime;

    @NotBlank
    @Pattern(regexp = "^[^ ]+$",message = "设备名不能包含空格")
    private String deviceName;
}
