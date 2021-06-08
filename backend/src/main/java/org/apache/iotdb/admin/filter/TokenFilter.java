package org.apache.iotdb.admin.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 */
public class TokenFilter implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("IOTDB")).build();
        try {
            String authorization = request.getHeader("Authorization");
            jwtVerifier.verify(authorization);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ErrorCode.Token_Err,ErrorCode.Token_Err_MSG);
        }
        return true;
    }
}
