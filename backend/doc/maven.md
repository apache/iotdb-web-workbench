## Maven 仓库配置

如何配置Maven仓库

## Maven 配置

Maven也请使用jdk 1.8，在Idea的Build Tools->Maven->Runner中设置JRE为1.8。

尝试执行右边的Maven Projects窗口中的Lifecycle中的compile命令，看是否编译成功，如失败可以添加`-X`参数，查看错误原因。

如果出现编译错误：Error:java: 无效的目标发行版: 11

请到 Build，Execution，Deployment->Compiler->Java Compiler设置 Per-module btyecode为1.8，默认可能为11。
