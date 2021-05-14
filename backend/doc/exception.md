## 异常组件

框架中包含了异常组件，异常组件的功能是：
1. 将异常封装为code、message实体类，通过http协议返回给前端应用。

```xml
<dependency>
    <groupId>cn.cisdigital</groupId>
    <artifactId>exception-component</artifactId>
    <version>1.0.0</version>
</dependency>
```

该组件为自研组件，注意事项有如下几点：

1、需要在启动类上加上@ComponentScan注解，意义：需要扫描异常组件下的相关异常处理类，将相应处理类组件的注入到spring中

@ComponentScan(value = {"cn.cisdigital.component.exc", "cn.cisdigital.demo"})第一个是异常组件的包名，第二个是自己项目的包名，需要根据包名变化而变化。

2、异常组件支持三大类异常的抛出，BaseException(继承Exception)，BaseRuntimeException(继承RuntimeException),还有Exception（其他异常，如空指针异常），
如果你需要自定义异常，则继承上面三大类异常抛出后就可以实现异常组件功能。

3、异常组件中用了@ControllerAdivce注解，如果你想在项目中再使用该注解来统一处理异常，需要以以下方式指定切面处理的优先级或者不使用异常逐渐。
``
@ControllerAdvice
@Order
``

如果你不需要这个库，可以把他去掉，但是我们强烈不建议你这么做。

## 返回值规范

