package org.apache.iotdb.admin.common.exception;

/**
 * @anthor fyx 2021/5/13
 */
public class BaseException extends Exception {

    private Integer errorCode;
    private String message;

    public BaseException(Integer errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
