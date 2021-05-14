
# 打包成docker

本项目可以直接通过命令打包成docker，首先，本项目打包成docker有2种方式：

1. 通过持续集成打包，这样只需要提供或修改本项目中的dockerfile文件，然后通过[持续集成](ci.md)文档进行配置即可。
2. 直接在本地通过dockerfile打包，本文档主要讲解本种方法

## 打包前的准备

我们建议你打包成docker前，必须mvn package编译一下系统，然后阅读一下dockerfile文件，检查一下如下的内容：

- 看一下里面有哪些需要修改，如应用的名字
- 需要copy的文件是否已经存在


## 在本地通过dockerfile打包成docker

首先确保你的电脑上安装了docker，并且能执行docker命令。

然后在项目的目录执行，必须在dockerfile的目录中执行：


```shell
docker build -t imagename:v1 . 
```

- imagename 是镜像的名字
- v1 是镜像的版本

执行这个命令后，就可以生成镜像了，通过

```shell
docker image 
```

可以发现你的镜像





   