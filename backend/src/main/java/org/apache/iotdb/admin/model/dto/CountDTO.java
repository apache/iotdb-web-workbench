package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CountDTO<T> implements Serializable {
    private List<T> objects;
    private Integer totalCount;
    private Integer totalPage;
}
