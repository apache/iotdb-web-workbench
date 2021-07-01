<template>
  <div>
    <div class="headerbox">
      <div class="flexBox" style="padding: 30px 0 10px 0">
        <div class="headerSpan">设备名称</div>
        <div class="flexBox headerIcon">
          <i class="el-icon-edit edit"></i>
          <i class="el-icon-delete delete"></i>
        </div>
      </div>
      <div class="messageBox">
        <span>数据链接：</span>
        <span>xxxxxxxxxxxxxxxxx</span>
        <span class="spanmargin">所属存储组：</span>
        <span>xxxxxxxx</span>
        <span class="spanmargin">设备描述：</span>
        <span>xxxxxxxx</span>
        <span class="spanmargin">创建人：</span>
        <span>xxxxxxxx</span>
        <span class="spanmargin">创建时间：</span>
        <span>xxxxxxxx</span>
      </div>
    </div>
    <div style="padding: 20px 30px" class="flexBox">
      <form-table :form="form"></form-table>
      <el-button class="creatButton">新建设备</el-button>
    </div>
    <stand-table
      :column="column"
      :tableData="tableData"
      :selectData="selectData"
      :lineHeight="5"
      :maxHeight="250"
      :exportData="() => {}"
    >
      <template #default="{ scope }">
        <el-button @click="searchRow(scope.row)" type="text" size="small">
          查看
        </el-button>
      </template>
    </stand-table>
    <div class="drawer" v-if="drawerFlag" :style="{ height: drawer + 'px' }">
      <div class="drawertitle">
        <div>xxxxx测试点数据趋势</div>
        <div>
          <i
            class="el-icon-close"
            style="cursor: pointer"
            @click="closeDrawer"
          ></i>
        </div>
      </div>
      <div style="padding: 10px 30px">
        <form-table :form="formdate"></form-table>
      </div>
      <div>
        <echarts></echarts>
      </div>
    </div>
  </div>
</template>

<script>
import { ElButton } from "element-plus";
import StandTable from "@/components/StandTable";
import FormTable from "@/components/FormTable";
import { reactive, ref } from "vue";
import Echarts from "@/components/Echarts";
export default {
  name: "DeviceMessage",
  setup() {
    let drawer = ref(0);
    let drawerFlag = ref(false);
    // const pagination = reactive({
    //   total: 50,
    //   currentPage: 1,
    // });
    const form = reactive({
      inline: true, //横向
      formData: {},
      formItem: [
        {
          label: "测点名称：", //名称
          type: "INPUT", //控件类型
          size: "small", //element尺寸
          width: "400px",
          itemID: "name", //数据字段名
          suffixIcon: "el-icon-search", // 后图标样式
          placeholder: "请输入测点名称", //灰色提示文字
        },
      ],
    });
    const formdate = reactive({
      inline: true, //横向
      formData: {
        time: [
          new Date("2021-05-09 23:59:59"),
          new Date("2021-05-10 23:59:59"),
        ],
      },
      formItem: [
        {
          label: "时间：", //名称
          type: "DATE", //控件类型
          size: "small", //element尺寸
          width: "400px",
          itemID: "time", //数据字段名
          // suffixIcon: "el-icon-search", // 后图标样式
          startPlaceholder: "开始日期", //灰色提示文字
          endPlaceholder: "结束日期", //灰色提示文字
        },
      ],
    });
    const column = [
      {
        label: "测点名称",
        prop: "name",
        value: "——", //默认值，该项如果没有数据显示
      },
      {
        label: "数据类型",
        prop: "type",
        value: "——", //默认值，该项如果没有数据显示
      },
      {
        label: "编码方式",
        prop: "func",
        value: "——", //默认值，该项如果没有数据显示
      },
      {
        label: "测点描述",
        prop: "mess",
        value: "——", //默认值，该项如果没有数据显示
      },
      {
        label: "最新值",
        prop: "num",
        value: "——", //默认值，该项如果没有数据显示
      },
      {
        label: "数据趋势",
        prop: "action",
      },
    ];
    const tableData = [
      {
        name: "A111",
        type: "string",
        func: "",
        mess: "设备",
      },
      {
        name: "A111",
        type: "string",
        func: "utf",
        mess: "设备",
      },
      {
        name: "A111",
        type: "string",
        func: "utf",
        mess: "设备111111111111111111111111111111111111111111111111111111111111",
      },
      {
        name: "A222",
        type: "string",
        func: "utf",
        mess: "设备",
      },
    ];
    function searchRow(a) {
      drawerFlag.value = true;
      setTimeout(() => {
        drawer.value = 400;
      }, 10);
      console.log(a);
      console.log(a.name);
    }
    function closeDrawer() {
      drawer.value = 0;
      setTimeout(() => {
        drawerFlag.value = true;
      }, 400);
    }
    function selectData(val) {
      console.log(1);
      console.log(val);
    }
    return {
      form,
      column,
      drawer,
      drawerFlag,
      tableData,
      formdate,
      searchRow,
      selectData,
      closeDrawer,
    };
  },
  components: {
    StandTable,
    FormTable,
    ElButton,
    Echarts,
  },
};
</script>

<style lang="scss" scoped>
$cursor: pointer;
.drawertitle {
  height: 35px;
  background: rgb(227, 227, 227);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  font-size: 11px;
}
.drawer {
  width: 84%;
  bottom: 0px;
  background: #fff;
  position: absolute;
  z-index: 9;
  overflow: hidden;
  transition: all 0.3s ease-out 0.1s;
  //   box-shadow: 3px 3px 3px #a2a2a2;
}
.headerbox {
  text-align: left;
  padding: 0 30px;
  border-bottom: 1px solid #ebeef5;
}
.headerSpan {
  font-size: 20px;
  font-weight: bold;
}
.flexBox {
  display: flex;
  justify-content: space-between;
}
.headerIcon {
  width: 50px;
  padding: 0 20px;
}
.edit {
  color: blue;
  cursor: $cursor;
}
.delete {
  color: red;
  cursor: $cursor;
}
.spanmargin {
  margin-left: 60px;
}
.messageBox {
  padding: 10px 0 20px 0;
  color: #909399;
  font-size: 14px;
}
.creatButton {
  padding: 0px 40px;
  height: 35px;
  min-height: 0px !important;
  background: #409eff;
  color: #fff;
  margin-top: 3px;
}
</style>
