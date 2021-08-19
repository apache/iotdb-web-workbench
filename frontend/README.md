# iotdb-fe

## 开发环境

目前支持的开发环境如下：

- node 10.0.0 及以上
- npm 6.0.0及以上
- 推荐使用visual studio code

## 安装依赖

```
cd frontend
npm install
```

## 运行项目

```
npm run serve
```

## 运行单元测试

```
npm run test:unit
```

## 技术栈

**Vue3.0** + **Scss**(css modules) + **Element Plus** + **Eslint,Stylelint,Prettier,Husky** 统一代码风格与规范。

## 代码规范

#### Javascript 规范

根据 eslint 约定规范，详见.eslintrc。

#### Css 规范

根据 stylelint 约定规范，详见.stylelintrc。

#### 代码格式风格规范

请 IDE 下载 Eslint 和 Prettier 插件，配置保存文件时自动 fix。
<br/>

#### 项目目录规范

```
src 源码目录
  |-- api 接口，统一管理
  |-- assets 静态资源，统一管理
  |-- components 公用业务组件，
  |-- hooks 共用hooks文件
  |-- i18n 国际化配置文件
  |-- components 公用组件，全局文件
  |-- util 全局工具
  |-- icons 图标，全局资源
  |-- lib  共用库
  |-- mock 模拟接口，临时存放
  |-- router 路由，统一管理
  |-- store vuex, 统一管理
  |-- styles 全局样式，scss全局变量等样式相关文件
  | |-- element.scss 全局修改element plus组件库样式
  | |-- reset.scss 重置浏览器样式
  | |-- variables.scss scss全局变量
  |-- views 视图目录
  | |-- pageA 页面
  | |-- |-- hooks 页面通用hooks
  | |-- |-- api 需要就近维护的接口
  | |-- |-- index.vue 页面入口页面
  | |-- |-- components 页面通用组件
```

#### UI 规范

原则上基于 Element Plus 组件库进行开发，统一开发样式。

#### git commit 规范

原则上基于 `<type>(<scope>): <subject>` 格式提交代码，详见https://zhuanlan.zhihu.com/p/182553920