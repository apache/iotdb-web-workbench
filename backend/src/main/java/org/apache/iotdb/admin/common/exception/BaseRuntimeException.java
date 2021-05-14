package org.apache.iotdb.admin.common.exception;


import org.apache.iotdb.admin.model.dto.CodeMsgDTO;

/**
 * @anthor fyx 2021/5/13
 */
public class BaseRuntimeException extends RuntimeException {
    private CodeMsgDTO codeMsgDTO;

    public BaseRuntimeException(CodeMsgDTO codeMsgDTO) {
        this.codeMsgDTO = codeMsgDTO;
    }

    public BaseRuntimeException(CodeMsgDTO codeMsgDTO, Throwable cause) {
        super(codeMsgDTO.toString(), cause);
    }

    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    public long getErrorCode() {
        return this.codeMsgDTO != null ? this.codeMsgDTO.getErrorCode() : -1L;
    }
}
