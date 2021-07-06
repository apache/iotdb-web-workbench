package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/7/6
 */
@Data
public class CountDTO<T> implements Serializable {
    private List<T> objects;
    private Integer totalCount;
    private Integer totalPage;
}
