package org.apache.iotdb.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @anthor fyx 2021/7/1
 */
@Data
public class Query implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "未指定所属连接id")
    private Integer connectionId;

    @NotNull(message = "未指定脚本名")
    private String queryName;

    private String sqls;
}
