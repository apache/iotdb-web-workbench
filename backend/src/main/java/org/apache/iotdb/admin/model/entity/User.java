package org.apache.iotdb.admin.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @anthor fyx 2021/5/24
 */

@Data
public class User implements Serializable {

    private int id;
    private String name;
    private String password;

}
