package org.apache.iotdb.admin.model.vo;

import lombok.Data;

/**
 * 返回信息类
 */
@Data
public class BaseVO<T> {

    /**
     * 0 表示成功 其他表示错误类型
     */
    private String code;

    /**
     * 定义出错时候用户可读的信息
     */
    private String message;

    /**
     * 这是一个返回数据的通用类型模板
     */
    private T data;

    public BaseVO() {
    }

    public BaseVO(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseVO<T> success(String message,T data) {
        return new BaseVO<>("0", message, data);
    }

}
