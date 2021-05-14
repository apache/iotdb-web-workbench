package org.apache.iotdb.admin.controller;
/**
 * @menu 测试接口
 */


import org.apache.iotdb.admin.bean.TestBean;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.config.DemoConfig;
import org.apache.iotdb.admin.model.dto.CodeMsgDTO;
import org.apache.iotdb.admin.model.entity.DemoUser;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.ResultWrapperVO;
import org.apache.iotdb.admin.service.DemoUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * demo controller，目的在提供编程的例子，你可以删除该文件，不会报错。
 *
 * @author fanli
 * @menu 订单业务接口test
 */
@RestController
public class DemoController {

    //    private final Logger logger = LoggerFactorgetLogger(this.getClass());

    /**
     * apollo测试组件
     */
    @Autowired
    private TestBean testBean;

    /**
     * apollo测试组件
     */
    @Autowired
    private DemoConfig demoConfig;

    @Autowired
    private DemoUserService demoUserService;

    @GetMapping(value = "/hello")
    @ApiOperation("接口文档")
    public BaseVO<String> hello() {
        return BaseVO.success("hello world");
    }

    /**
     * 获取数据库中的信息
     *
     * @param: [tables]
     * @return: BaseVO
     */
    @GetMapping(value = "/db")
    public BaseVO<ResultWrapperVO<List<DemoUser>>> selectDb(int tables, String b) {
        List<DemoUser> userList = demoUserService.list();
        userList.forEach(System.out::println);
        ResultWrapperVO<List<DemoUser>> result = new ResultWrapperVO<>();
        result.setCode(0L);
        result.setMessage("");
        result.setData(userList);
        return BaseVO.success(result);
    }


    /**
     * 异常组件使用demo，一般情况，只需要抛出BaseException异常，异常组件会自动捕获异常，并以json
     * 的形式返回给前端程序。
     * 如果要自定义一些异常，那么需要继承BaseException异常。
     */
    @GetMapping(value = "/exception")
    public void exceptionTest() throws BaseException {
        CodeMsgDTO codeMsgDTO = new CodeMsgDTO(11111111, "这是一个异常组件的例子");
        throw new BaseException(codeMsgDTO);
    }


    /**
     * apollo测试组件,没有返回值中data为空
     */
    @GetMapping(value = "/apollo")
    public BaseVO<Object> apolloTest() {

        //测试apollo读取配置
        System.out.println(testBean.toString());
        System.out.println(demoConfig.getUrl());
        return BaseVO.success();
    }

    /**
     * mybatis—plus分页，使用该方式分页必须要写MybatisPlusConfig配置类，配置分页插件
     */
    @GetMapping(value = "/list/user")
    public BaseVO<Page<DemoUser>> listAllUser() {
        Page<DemoUser> page = new Page<>(1, 3);
        //查询年龄为1的demoUser 用户，并按照页面大小为3，当前页数为1的形式进行展示
        return BaseVO.success(demoUserService.lambdaQuery().eq(DemoUser::getAge, 1).page(page));
    }

    /**
     * 通过xml写sql语句的方式。一般不建议用此方式，除非复杂的sql连表语句
     *
     * @return 返回对象
     */
    @GetMapping(value = "/test/list")
    public BaseVO<List<DemoUser>> getUserByXml() {
        return BaseVO.success(demoUserService.selectByXml());
    }
}
