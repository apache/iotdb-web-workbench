package org.apache.iotdb.admin.common.exception;

/**
 * 异常类
 */
public class BaseException extends Exception {

    private String errorCode;
    private String message;

    public BaseException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
