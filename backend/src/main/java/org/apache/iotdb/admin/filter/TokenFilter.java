package org.apache.iotdb.admin.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 拦截器
 */
public class TokenFilter implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws BaseException {
        JWTVerifier jwtVerifier;
        try {
            jwtVerifier = JWT.require(Algorithm.HMAC256("IOTDB:" + InetAddress.getLocalHost().getHostAddress())).build();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new BaseException(ErrorCode.SET_JWT_FAIL, ErrorCode.SET_JWT_FAIL_MSG);
        }
        try {
            String authorization = request.getHeader("Authorization");
            jwtVerifier.verify(authorization);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ErrorCode.Token_Err, ErrorCode.Token_Err_MSG);
        }
        return true;
    }
}
