package org.apache.iotdb.admin.model.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

//import javax.validation.constraints.NotNull;

/**
 * 目的在提供编程的例子，你可以删除Demo开始的文件，不会报错。
 * <p>
 * 该实体类与数据库表对应
 *
 * @author fanli
 */
@Data
public class DemoUser {

    private Long id;

    /**
     * 校验注解
     */
    @NotNull
    @Length(min = 1, max = 100)
    private String name;
    private Integer age;
    private String email;
}
