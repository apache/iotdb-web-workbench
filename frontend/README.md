# iotdb-fe

## 安装依赖
```
npm install
```
## 运行项目
```
npm run serve
```
## 技术栈
vue3.0+scss(css modules)+Element Plus+eslint,stylelint,prettier,husky 统一代码风格与规范。

## 代码规范
* Javascript 规范
根据 eslint 约定规范，详见.eslintrc。
<br/>
* Css 规范
根据 stylelint 约定规范，详见.stylelintrc。
<br/>
* 代码格式风格规范
请 IDE 下载 eslint 和 prettier 插件，配置保存文件时自动fix。
<br/>
* 项目目录规范
src 源码目录
|-- api 接口，统一管理
|-- assets 静态资源，统一管理
|-- components 公用组件，全局文件
|-- util 全局工具
|-- icons 图标，全局资源
|-- lib  外部引用的插件存放及修改文件
|-- mock 模拟接口，临时存放
|-- router 路由，统一管理
|-- store  vuex, 统一管理
|-- views 视图目录
|   |-- staffWorkbench 视图模块名
|   |-- |-- api 需要就近维护的接口文件夹
|   |-- |-- index.vue 模块入口页面
|   |-- |-- indexComponents 模块页面级组件文件夹
|   |-- |-- components 模块通用组件文件夹
<br/>
* UI 规范
原则上基于 Element Plus 组件库进行开发，统一开发样式。
<br/>
* git commit 规范
原则上基于 `<type>(<scope>): <subject>` 格式提交代码，详见[链接](https://zhuanlan.zhihu.com/p/182553920)
