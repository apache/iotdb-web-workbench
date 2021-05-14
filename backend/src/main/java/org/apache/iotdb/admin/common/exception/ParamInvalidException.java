package org.apache.iotdb.admin.common.exception;

import org.apache.iotdb.admin.model.dto.CodeMsgDTO;

/**
 * 参数校验异常，继承异常组件中的异常后，抛出会被拦截并返回特定的状态码
 *
 * @author 辰行
 * @date 2020/10/15
 */
public class ParamInvalidException extends BaseRuntimeException {
    public ParamInvalidException(CodeMsgDTO codeMsgDTO) {
        super(codeMsgDTO);
    }

    public ParamInvalidException(CodeMsgDTO codeMsgDTO, Throwable cause) {
        super(codeMsgDTO, cause);
    }

    public ParamInvalidException(Throwable cause) {
        super(cause);
    }
}
