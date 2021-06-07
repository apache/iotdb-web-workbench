package org.apache.iotdb.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @anthor fyx 2021/5/25
 */

@Data
public class Connection implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Pattern(regexp = "^(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})(\\.(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})){3}$",message = "主机号输入不合法")
    private String host;

    @Range(min = 0,max = 65535,message = "端口号输入不合法")
    private Integer port;

    @Length(min = 4,message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String username;

    @Length(min = 4,message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String password;

    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String alias;

    private Integer userId;

}
