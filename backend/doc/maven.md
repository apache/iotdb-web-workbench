## Maven 仓库配置

如何配置Maven仓库

## Maven 安装

无论你是mac电脑还是window电脑，都可以到Maven官网直接下载[Maven 3.8.2](https://dlcdn.apache.org/maven/maven-3/3.8.2/binaries/apache-maven-3.8.2-bin.tar.gz)

然后找一个目录解压，路径最好不要有中文，然后在Idea中引用该Maven。在下图中找到"Maven Setting"

![](image/maven配置.jpg)

然后按照如下内容进行配置

![](image/maven详细配置.jpg)

## Maven 配置

Maven也请使用jdk 1.8，在Idea的Build Tools->Maven->Runner中设置JRE为1.8。

尝试执行右边的Maven Projects窗口中的Lifecycle中的compile命令，看是否编译成功，如失败可以添加`-X`参数，查看错误原因。

如果出现编译错误：Error:java: 无效的目标发行版: 11

请到 Build，Execution，Deployment->Compiler->Java Compiler设置 Per-module btyecode为1.8，默认可能为11。
