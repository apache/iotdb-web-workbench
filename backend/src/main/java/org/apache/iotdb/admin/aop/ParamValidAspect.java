package org.apache.iotdb.admin.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.Set;

/**
 *
 * @anthor fyx 2021/5/27
 */
@Aspect
@Slf4j
@Component
public class ParamValidAspect {

    @Autowired
    private Validator validator;


    /**
     * 定义校验包的未知
     */
    @Pointcut("execution(* org.apache.iotdb.admin.controller..*.*(..))")
    public void pointcut() {
    }

    /**
     * 处理参数校验错误的具体方法，抛出异常组件中的异常，遵循异常规范
     */
    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                Set<ConstraintViolation<Object>> constraintViolations = validator.validate(arg);
                if (constraintViolations.size() > 0) {
                    for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
                        Path property = constraintViolation.getPropertyPath();
                        String name = property.iterator().next().getName();
                        //打印具体异常信息，对应校验框架注解中的message
                        log.error("[" + name + "]" + constraintViolation.getMessage());
                        throw new BaseException(4001,"参数错误:"+"[" + name + "]" + constraintViolation.getMessage());
                    }
                }
            }
        }
        return pjp.proceed();
    }
}
