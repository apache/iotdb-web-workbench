package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/7/7
 */
@Data
public class SearchDTO implements Serializable {

    private List<String> sqls;

    @NotNull(message = "不能为null")
    private Long timestamp;

}
