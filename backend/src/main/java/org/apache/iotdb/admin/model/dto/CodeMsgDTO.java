package org.apache.iotdb.admin.model.dto;



import org.apache.iotdb.admin.common.constant.HttpCodeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @anthor fyx 2021/5/13
 */
public class CodeMsgDTO implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(CodeMsgDTO.class);
    Long errorCode;
    Long code;
    String message;

    public CodeMsgDTO(long errorCode, String message) {
        if (errorCode < 0L) {
            throw new IllegalArgumentException("The error status_code must not be less than 0.errorCode[" + errorCode + "]");
        } else {
            this.errorCode = errorCode;
            this.message = message;
            this.code = errorCode;
            HttpCodeConstant.putHttpCode(errorCode, this.code, message);
        }
    }

    public Long getErrorCode() {
        return this.errorCode;
    }

    public Long getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
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
        } else if (!(o instanceof CodeMsgDTO)) {
            return false;
        } else {
            CodeMsgDTO other = (CodeMsgDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$errorCode = this.getErrorCode();
                    Object other$errorCode = other.getErrorCode();
                    if (this$errorCode == null) {
                        if (other$errorCode == null) {
                            break label47;
                        }
                    } else if (this$errorCode.equals(other$errorCode)) {
                        break label47;
                    }

                    return false;
                }

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
        return other instanceof CodeMsgDTO;
    }

    @Override
    public int hashCode() {
        int result = 1;
        Object $errorCode = this.getErrorCode();
        result = result * 59 + ($errorCode == null ? 43 : $errorCode.hashCode());
        Object $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CodeMsgDTO(errorCode=" + this.getErrorCode() + ", code=" + this.getCode() + ", message=" + this.getMessage() + ")";
    }
}
