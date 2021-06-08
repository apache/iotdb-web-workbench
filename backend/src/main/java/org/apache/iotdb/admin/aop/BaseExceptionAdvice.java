package org.apache.iotdb.admin.aop;


import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 通用异常处理拦截器
 */
@Slf4j
@ControllerAdvice
public class BaseExceptionAdvice {


    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public BaseVO handleBaseException(BaseException e) {
        log.error("调用接口异常", e);
        BaseVO result = new BaseVO(e.getErrorCode(),e.getMessage(),null);
        return result;
    }

    /**
     * 处理自定义异常
     */
    private BaseVO handleCustomException(long errorCode) {
        return null;
    }
}
