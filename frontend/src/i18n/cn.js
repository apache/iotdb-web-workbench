import zhLocale from "element-plus/lib/locale/lang/zh-cn";

const cn = {
  [zhLocale.name]: {
    el: zhLocale.el,
    message: {
      hello: "你好，世界",
    },
    rootPage: {
      chinalang: "中文",
      englishlang: "英文",
      loginoutText: "退出登录",
      about: "关于我们",
      help: "帮助手册",
      databaseManagement: "数据库管理",
      dataList: "数据列表",
      newdatasource: "新建数据源",
      newQueryWindow: "新建查询窗口",
    },
    loginPage: {
      account: "账号",
      password: "密码",
      placeholderAccount: "请输入账号",
      placeholderPassword: "请输入密码",
      forgetPassWord: "忘记密码",
      signIn: "登录",
      forgetPassword: "忘记密码",
      forgetPasswordTip: "请联系系统管理员",
      accountEmptyTip: "账号不能为空",
      passwordEmptyTip: "密码不能为空",
      welcomeLogin: "登录IOTDB数据库管理",
    },
    device: {
      devicename: "实体名称",
      description: "实体描述",
      group: "所属存储组",
      physical: "实体物理量",
      addphysical: "添加物理量",
      delete: "删除",
      cencel: "取消",
      ok: "确定",
      physicalname: "物理量名称",
      datatype: "数据类型",
      codingmode: "编码方式",
      physicaldescr: "物理量描述",
      action: "操作",
    },
  },
};

export default cn;
