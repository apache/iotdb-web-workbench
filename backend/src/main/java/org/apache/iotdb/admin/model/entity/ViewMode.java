package org.apache.iotdb.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

import java.io.Serializable;

/**
 * @author Erickin
 * @create 2022-04-22-上午 10:35
 */
@Data
@TableName("view_mode")
public class ViewMode implements Serializable {
  private static final long serialVersionUID = 1L;

  @Null
  @TableId(type = IdType.AUTO)
  private Integer id;

  @NotBlank
  @Pattern(regexp = "^[^ ]+$", message = "The account name cannot contain spaces")
  private String name;
}
