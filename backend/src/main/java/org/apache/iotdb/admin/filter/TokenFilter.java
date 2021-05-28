package org.apache.iotdb.admin.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @anthor fyx 2021/5/24
 */
public class TokenFilter implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("IOTDB")).build();
        try {
            jwtVerifier.verify(request.getHeader("Authorization"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(0,"请登录或token失效");
        }
        return true;
    }
}
