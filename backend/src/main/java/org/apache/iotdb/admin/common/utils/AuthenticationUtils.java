package org.apache.iotdb.admin.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验工具类
 */
public class AuthenticationUtils {

    public static void userAuthentication(Integer userId, HttpServletRequest request) throws BaseException {
        if(userId == null){
            throw new BaseException(ErrorCode.NO_USER,ErrorCode.NO_USER_MSG);
        }
        DecodedJWT authorization = JWT.decode(request.getHeader("Authorization"));
        Integer tokenUserId = authorization.getClaim("userId").asInt();
        if(!tokenUserId.equals(userId)){
            throw new BaseException(ErrorCode.USER_AUTH_FAIL,ErrorCode.USER_AUTH_FAIL_MSG);
        }
    }

    public static Integer getUserId(HttpServletRequest request){
        DecodedJWT authentication = JWT.decode(request.getHeader("Authorization"));
        return authentication.getClaim("userId").asInt();
    }
}
