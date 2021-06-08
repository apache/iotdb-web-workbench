package org.apache.iotdb.admin.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
import org.apache.iotdb.admin.model.entity.User;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.ConnVO;
import org.apache.iotdb.admin.model.vo.ConnectionVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.admin.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;


@RestController
@Api(value = "登录相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionService connectionService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    @ApiOperation("登录")
    public BaseVO<ConnectionVO> login(@RequestParam("name")String name, @RequestParam("password") String password, HttpServletResponse response) throws BaseException {
        if(name == null || password == null || name.length() < 4 || password.length() < 4){
            throw new BaseException(ErrorCode.WRONG_USER_PARAM,ErrorCode.WRONG_USER_PARAM_MSG);
        }
        User user = userService.login(name, password);
        int userId = user.getId();
        List<ConnVO> connVOs = connectionService.getAllConnections(userId);
        ConnectionVO connectionVO = new ConnectionVO(connVOs,userId);
        response.addHeader("Authorization",getToken(user));
        return BaseVO.success("登录成功",connectionVO);
    }

    @PostMapping("/save")
    @ApiOperation("创建或修改用户")
    public BaseVO save(@RequestBody User user) throws BaseException {
        userService.insert(user);
        return BaseVO.success("保存成功",null);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除用户")
    public BaseVO delete(@RequestParam("userId") Integer userId, HttpServletRequest request) throws BaseException {
        AuthenticationUtils.userAuthentication(userId,request);
        userService.delete(userId);
        return BaseVO.success("保存成功",null);
    }

    private String getToken(User user) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR,24);
        String token  = JWT.create()
                .withClaim("userId", user.getId())
                .withClaim("name", user.getName())
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256("IOTDB"));
        logger.info(user.getName()+"登录成功");
        return token;
    }

}
