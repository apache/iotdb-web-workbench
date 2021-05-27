package org.apache.iotdb.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/5/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionVO implements Serializable {
    List<ConnVO> aliasList;
    Integer userId;
}
