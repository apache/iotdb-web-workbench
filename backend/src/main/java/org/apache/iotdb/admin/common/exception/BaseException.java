package org.apache.iotdb.admin.common.exception;

import org.apache.iotdb.admin.model.dto.CodeMsgDTO;

/**
 * @anthor fyx 2021/5/13
 */
public class BaseException extends Exception {
    private CodeMsgDTO codeMsgDTO;

    public BaseException(CodeMsgDTO codeMsgDTO) {
        this.codeMsgDTO = codeMsgDTO;
    }

    public BaseException(CodeMsgDTO codeMsgDTO, Throwable cause) {
        super(codeMsgDTO.toString(), cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public long getErrorCode() {
        return this.codeMsgDTO != null ? this.codeMsgDTO.getErrorCode() : -1L;
    }
}
