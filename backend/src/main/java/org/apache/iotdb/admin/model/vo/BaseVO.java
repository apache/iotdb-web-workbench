package org.apache.iotdb.admin.model.vo;

import lombok.Data;

/**
 * @author 辰行
 * @date 2020/10/10
 */
@Data
public class BaseVO<T> {

    /**
     * 200 表示成功 0表示失败
     */
    private Integer code;

    /**
     * 定义出错时候用户可读的信息
     */
    private String message;

    /**
     * 这是一个返回数据的通用类型模板
     */
    private T data;

    public BaseVO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseVO<T> success(T data) {
        return new BaseVO<>(0, "成功", data);
    }

    public static <T> BaseVO<T> success() {
        return new BaseVO<>(0, "成功", null);
    }

}
