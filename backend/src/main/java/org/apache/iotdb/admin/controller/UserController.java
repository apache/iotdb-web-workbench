package org.apache.iotdb.admin.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.entity.User;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.ConnVO;
import org.apache.iotdb.admin.model.vo.ConnectionVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;

/**
 * @anthor fyx 2021/5/24
 */

@RestController
@Api(value = "登录相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("/login")
    @ApiOperation("登录")
    public BaseVO<ConnectionVO> login(@RequestParam("name")String name, @RequestParam("password") String password, HttpServletResponse response) throws BaseException {
        User user = userService.login(name, password);
        int userId = user.getId();
        List<ConnVO> connVOs = connectionService.getAllConnections(userId);
        ConnectionVO connectionVO = new ConnectionVO(connVOs,userId);
        response.addHeader("Authorization",getToken(user));
        return new BaseVO<>(200,"登录成功",connectionVO);
    }

    private String getToken(User user) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR,24);
        String token  = JWT.create()
                .withClaim("userId", user.getId())
                .withClaim("name", user.getName())
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256("IOTDB"));
        System.out.println(user.getName()+"登录成功");
        return token;
    }

}
