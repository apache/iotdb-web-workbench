package org.apache.iotdb.admin.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

/** 传输role信息类 */
@Data
public class IotDBRole implements Serializable {

  private Integer id;

  @NotNull
  @Length(min = 4, message = "角色名长度必须大于等于4")
  private String roleName;

  private String description;
}
