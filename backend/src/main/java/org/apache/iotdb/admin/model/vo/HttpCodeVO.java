package org.apache.iotdb.admin.model.vo;

import java.beans.ConstructorProperties;

/**
 * @anthor fyx 2021/5/13
 */
public class HttpCodeVO {
    private Long code;
    private String message;

    public Long getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof HttpCodeVO)) {
            return false;
        } else {
            HttpCodeVO other = (HttpCodeVO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$code = this.getCode();
                Object other$code = other.getCode();
                if (this$code == null) {
                    if (other$code != null) {
                        return false;
                    }
                } else if (!this$code.equals(other$code)) {
                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof HttpCodeVO;
    }

    @Override
    public int hashCode() {
        int result = 1;
        Object $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "HttpCodeVO(code=" + this.getCode() + ", message=" + this.getMessage() + ")";
    }

    public HttpCodeVO() {
    }

    @ConstructorProperties({"code", "message"})
    public HttpCodeVO(Long code, String message) {
        this.code = code;
        this.message = message;
    }
}
