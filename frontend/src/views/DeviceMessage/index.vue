<template>
  <div class="content_message">
    <div class="content_padding">
      <div class="headerbox">
        <div class="flexBox" style="padding: 15px 0 10px 0">
          <div class="headerSpan">{{ routeData.obj.name }}</div>
          <div class="flexBox headerIcon">
            <el-button @click="editDevce">
              <svg class="icon edit" aria-hidden="true">
                <use xlink:href="#icon-se-icon-f-edit"></use></svg
              >&nbsp;编辑</el-button
            >
            <el-button @click="deleteData">
              <svg class="icon delete" aria-hidden="true">
                <use xlink:href="#icon-se-icon-delete"></use></svg
              >&nbsp;删除</el-button
            >
          </div>
        </div>
        <div class="messageBox">
          <span>{{ $t('device.dataconnection') }}：</span>
          <span>{{ routeData.obj.parentids }}</span>
          <!-- <span class="spanmargin">{{ $t('device.group') }}：</span>
        <span>{{ routeData.obj.storagegroupid }}</span> -->
          <span class="spanmargin">{{ $t('device.description') }}：</span>
          <span>{{ deviceObj.deviceData.description }}</span>
          <span class="spanmargin">
            <svg class="icon adminIcon" aria-hidden="true">
              <use xlink:href="#icon-yh"></use>
            </svg>
            {{ $t('device.creator') }}：
          </span>
          <span>{{ deviceObj.deviceData.creator }}</span>
          <span class="spanmargin">{{ $t('device.createTime') }}：</span>
          <span>{{ deviceObj.deviceData.time }}</span>
        </div>
      </div>
    </div>
    <el-tabs v-model="activeName" @tab-click="handleClick" class="tab_class">
      <el-tab-pane label="实体结构" name="first">
        <div class="frist_div">
          <div style="padding: 0px 0px 10px 0px" class="flexBox">
            <form-table :form="form" @serchFormData="serchFormData"></form-table>
            <!-- <el-button type="primary" class="newButton" @click="creatDevice">{{ $t('storagePage.newDevice') }}</el-button> -->
          </div>
          <stand-table
            :column="column"
            :tableData="tableData"
            :getList="getListData"
            :total="totalCount"
            :lineHeight="10"
            :lineWidth="21"
            :celineWidth="23"
            :maxHeight="450"
            :pagination="pagination"
            @getPagintions="getPagintions"
          >
            <template #default="{ scope }">
              <div @click="searchRow(scope.row)" v-if="scope.row.newValue * 1">
                <action :echartsData="routeData.obj" :row="scope.row" :scope="scope"></action>
              </div>
              <div v-else class="actionSpan">
                <span>——</span>
              </div>
            </template>
          </stand-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="数据预览" name="second">
        <div class="frist_div">
          <div style="padding: 0px 0px 10px 0px" class="flexBox">
            <div class="button_div">
              <el-dropdown trigger="click">
                <el-button type="primary">新增数据</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="dialogFormVisible.flag = true">随机生成</el-dropdown-item>
                    <el-dropdown-item @click="dialogVisible1.flag = true">批量导入</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button>导出数据</el-button>
            </div>
            <div>
              <form-table :form="form1" @serchFormData="serchFormData"></form-table>
            </div>
          </div>
          <stand-table
            :column="column1"
            :tableData="tableData"
            :getList="getListData"
            :total="totalCount"
            :lineHeight="10"
            :celineHeight="10"
            :maxHeight="450"
            :pagination="pagination"
            :selectData="selectData"
            :deleteArry="deleteArry"
            @getPagintions="getPagintions"
          >
            <template #default="{ scope }">
              <span class="table2 edit">编辑</span>
              <span class="table2 delete" @click="deleteRow(scope.row, scope.$index)">{{ $t('device.delete') }}</span>
              <!-- <el-button @click="deleteRow(scope.row, scope.$index)" type="text" size="small" style="color: red"> 编辑 </el-button>
              <el-button @click="deleteRow(scope.row, scope.$index)" type="text" size="small" style="color: red">
                {{ $t('device.delete') }}
              </el-button> -->
            </template>
          </stand-table>
        </div>
      </el-tab-pane>
    </el-tabs>
    <div class="drawer" v-if="drawerFlag" :style="{ height: drawer + 'px', width: widths - dividerWidth + 'px' }">
      <div class="drawertitle">
        <div>{{ routeData.obj.timeseries }}{{ $t('device.datatrend') }}</div>
        <div>
          <i class="el-icon-close" style="cursor: pointer" @click="closeDrawer"></i>
        </div>
      </div>
      <div class="formtable">
        <form-table :form="formdate"></form-table>
      </div>
      <div>
        <echarts ref="drawerRef" :echartsData="routeData.obj" :getDate="getDate"></echarts>
      </div>
    </div>
    <el-dialog title="随机数据" v-model="dialogFormVisible.flag" width="500px">
      <div>
        <span style="color: red">提示：同时间戳的数据将会被覆盖</span>
        <div style="padding-top: 20px">
          <form-table ref="formTable" :form="newData"></form-table>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible.flag = false">取 消</el-button>
          <el-button type="primary" @click="getDataLook">确 定</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog title="导入" v-model="dialogVisible1.flag" width="500px" @close="closeEd" class="export_form">
      <input ref="filesd" type="file" multiple="multiple" style="display: none" @change="getfile" />
      <el-form ref="ruleForm" label-width="100%">
        <el-form-item label="模板下载：">
          <el-button size="small" @click="downloadEx"
            >下载模板
            <i class="iconfont se-icon-download"></i>
          </el-button>
        </el-form-item>
        <el-form-item label="选择文件：" prop="categoryName">
          <el-button type="primary" size="small" @click="selectFile"
            >选择文件
            <i class="iconfont se-icon-upload2"></i>
          </el-button>
          <span class="export_notice">仅支持csv格式文件</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible1.flag = false">取 消</el-button>
          <el-button type="primary" @click="dialogVisible1.flag = false">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessageBox, ElMessage, ElButton, ElTabs, ElTabPane, ElDropdown, ElDropdownMenu, ElDropdownItem, ElDialog, ElForm, ElFormItem } from 'element-plus';
import StandTable from '@/components/StandTable';
import FormTable from '@/components/FormTable';
import { reactive, ref, onActivated } from 'vue';
import { getList, getDeviceDate, deleteDevice } from './api';
import Echarts from '@/components/Echarts';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import action from './components/action.vue';
export default {
  name: 'DeviceMessage',
  props: {
    func: Object,
    data: Object,
    dividerWidth: Object,
  },
  setup(props) {
    const { t } = useI18n();
    const funcs = reactive(props.func);
    const router = useRouter();
    const route = useRoute();
    const dialogFormVisible = reactive({
      flag: false,
    });
    const dialogVisible1 = reactive({
      flag: false,
    });
    let widths = ref(window.screen.width);
    let drawer = ref(0);
    let loading = ref(true);
    let drawerFlag = ref(false);
    let activeName = ref('first');
    let connection = ref('');
    let totalCount = ref(0);
    const filesd = ref(null);
    const pagination = reactive({
      pageSize: 10,
      pageNum: 1,
    });
    const routeData = reactive({
      obj: route.params,
    });
    const drawerRef = ref(null);
    const formTable = ref(null);
    let deviceObj = reactive({
      deviceData: 0,
    });
    const form = reactive({
      inline: true, //横向
      formData: {},
      formItem: [
        {
          label: '', //名称
          type: 'INPUT', //控件类型
          size: 'small', //element尺寸
          width: '400px',
          itemID: 'keyword', //数据字段名
          suffixIcon: 'el-icon-search', // 后图标样式
          placeholder: 'device.serchPy', //灰色提示文字
        },
      ],
    });
    const form1 = reactive({
      inline: true, //横向
      formData: {
        time: [],
      },
      formItem: [
        {
          label: 'device.time', //名称
          type: 'DATE', //控件类型
          size: 'small', //element尺寸
          width: '400px',
          itemID: 'time', //数据字段名
          // suffixIcon: "el-icon-search", // 后图标样式
          startPlaceholder: '开始日期', //灰色提示文字
          endPlaceholder: '结束日期', //灰色提示文字
        },
        {
          label: '物理量筛选', //名称
          type: 'SELECT', //控件类型
          size: 'small', //element尺寸
          width: '150px',
          itemID: 'keyword', //数据字段名
          placeholder: 'device.serchPy', //灰色提示文字
          options: [
            { label: '全部', value: '1' },
            { label: '启动', value: '1' },
          ],
        },
      ],
    });
    const newData = reactive({
      formData: {
        keyword: new Date(),
        mm: null,
        ms: null,
      },
      formItem: [
        {
          label: '开始时间', //名称
          type: 'DATETIME', //控件类型
          size: 'small', //element尺寸
          width: '100%',
          itemID: 'keyword', //数据字段名
          placeholder: '选择时间', //灰色提示文字
          required: true,
          rules: [{ required: true, message: '请选择开始时间', trigger: 'blur' }],
        },
        {
          label: '步长', //名称
          type: 'INPUTNUM', //控件类型
          size: 'small', //element尺寸
          width: '100%',
          itemID: 'mm', //数据字段名
          placeholder: '请输入步长', //灰色提示文字
          unit: 'ms',
          required: true,
          rules: [
            { required: true, message: '请填写步长', trigger: 'blur' },
            {
              trigger: 'change',
              validator: async (rule, value, callback) => {
                if (!/^[0-9]*$/.test(value) || value < 0) {
                  callback(new Error('请输入正整数'));
                  return;
                }
              },
            },
          ],
        },
        {
          label: '生成数量', //名称
          type: 'INPUTNUM', //控件类型
          size: 'small', //element尺寸
          width: '100%',
          itemID: 'ms', //数据字段名
          placeholder: '请输入生成数量', //灰色提示文字
          required: true,
          rules: [
            { required: true, message: '请输入生成数量', trigger: 'blur' },
            {
              trigger: 'change',
              validator: async (rule, value, callback) => {
                if (!/^[0-9]*$/.test(value)) {
                  callback(new Error('请输入正整数'));
                  return;
                }
                if (value > 1000000) {
                  callback(new Error('限制100万条以内'));
                  return;
                }
              },
            },
          ],
        },
      ],
    });
    const formdate = reactive({
      inline: true, //横向
      formData: {
        time: [],
      },
      formItem: [
        {
          label: 'device.time', //名称
          type: 'DATE', //控件类型
          size: 'small', //element尺寸
          width: '400px',
          itemID: 'time', //数据字段名
          // suffixIcon: "el-icon-search", // 后图标样式
          startPlaceholder: '开始日期', //灰色提示文字
          endPlaceholder: '结束日期', //灰色提示文字
          Event: checkDateValue,
        },
      ],
    });
    const column = reactive({
      list: [
        {
          label: 'device.physicalname',
          prop: 'timeseries',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: 'device.datatype',
          prop: 'dataType',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: 'device.codingmode',
          prop: 'encoding',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: 'device.physicaldescr',
          prop: 'description',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: 'device.newValue',
          prop: 'newValue',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: 'device.datatrends',
          prop: 'action',
        },
      ],
    });
    const column1 = reactive({
      list: [
        {
          label: '时间戳',
          prop: 'timeseries',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '物理量名',
          prop: 'dataType',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '物理量名',
          prop: 'encoding',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '物理量名',
          prop: 'description',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '物理量名',
          prop: 'newValue',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '物理量名',
          prop: 'newValue',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '物理量名',
          prop: 'newValue',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: '物理量名',
          prop: 'newValue',
          value: '——', //默认值，该项如果没有数据显示
        },
        {
          label: 'device.action',
          prop: 'action',
          width: 100,
        },
      ],
    });
    const tableData = reactive({
      list: [],
    });
    function selectFile() {
      filesd.value.click();
    }
    function getDataLook() {
      formTable.value.checkData(dialogFormVisible);
    }
    function searchRow(val) {
      routeData.obj.timeseries = val.timeseries;
      if (!drawerFlag.value) {
        drawerFlag.value = true;
      } else {
        drawerFlag.value = false;
        setTimeout(() => {
          drawerFlag.value = true;
          drawerRef.value.getehartsData(routeData.obj);
        }, 10);
      }
      setTimeout(() => {
        drawer.value = 400;
      }, 10);
    }
    function closeDrawer() {
      drawer.value = 0;
      setTimeout(() => {
        drawerFlag.value = false;
      }, 400);
    }
    function selectData(val) {
      console.log(val);
    }
    function deleteArry() {
      alert(1234);
    }
    function getDate(timeS, timeE) {
      formdate.formData.time = [timeS, timeE];
    }
    function creatDevice() {
      funcs.addTab(`${routeData.obj.parentid}:newdevice`);
    }
    function editDevce() {
      router.push({ name: 'Device', params: { ...routeData.obj } });
    }
    function deleteData() {
      ElMessageBox.confirm(`${t('device.deletecontent1')}"${routeData.obj.name}"？${t('device.deletecontent2')}`, '提示', {
        confirmButtonText: t('device.ok'),
        cancelButtonText: t('device.cencel'),
        type: 'warning',
      })
        .then(() => {
          deleteDevice(routeData.obj).then(() => {
            ElMessage({
              type: 'success',
              message: `${t('device.deleteSuccess')}!`,
            });
            props.func.updateTree();
            props.func.removeTab(routeData.obj.id);
          });
        })
        .catch(() => {
          ElMessage({
            type: 'info',
            message: t('device.cencel'),
          });
        });
    }
    function serchFormData() {
      pagination.pageSize = 10;
      pagination.pageNum = 1;
      getListData();
    }
    function getPagintions(val) {
      console.log(val);
      console.log(pagination);
    }
    function getListData() {
      getList(routeData.obj, { ...pagination, ...form.formData }).then((res) => {
        tableData.list = res.data.measurementVOList;
        totalCount.value = res.data.totalCount;
      });
    }
    function getdData() {
      getDeviceDate(routeData.obj).then((res) => {
        deviceObj.deviceData = res.data;
      });
    }
    function checkDateValue() {
      console.log(formdate.formData.time);
      drawerRef.value.setEchartsTime(formdate.formData.time);
    }
    onActivated(() => {
      routeData.obj = Object.assign(routeData.obj, route.params);
      form.formData.keyword = '';
      if (route.params.forceupdate === 'true') {
        formdate.formData.time = [];
      }
      setTimeout(() => {
        getdData();
        getListData();
      }, 500);
    });
    return {
      selectFile,
      filesd,
      dialogVisible1,
      getDataLook,
      formTable,
      newData,
      dialogFormVisible,
      deleteArry,
      column1,
      form1,
      activeName,
      form,
      widths,
      column,
      drawer,
      connection,
      getDate,
      drawerRef,
      deviceObj,
      routeData,
      deleteData,
      pagination,
      totalCount,
      getPagintions,
      getListData,
      serchFormData,
      drawerFlag,
      tableData,
      formdate,
      searchRow,
      selectData,
      editDevce,
      closeDrawer,
      creatDevice,
      loading,
    };
  },
  components: {
    StandTable,
    FormTable,
    ElButton,
    Echarts,
    action,
    ElTabs,
    ElTabPane,
    ElDropdown,
    ElDropdownMenu,
    ElDropdownItem,
    ElDialog,
    ElForm,
    ElFormItem,
  },
};
</script>

<style lang="scss" scoped>
$cursor: pointer;
.export_notice {
  margin-left: 20px;
  color: #abb1c7;
}
.button_div {
  display: flex;
  width: 180px;
  justify-content: space-between;
}
.table2 {
  cursor: pointer;
  &.edit {
    color: #16c493;
  }
  &.delete {
    color: red;
    margin-left: 15px;
  }
}
.frist_div {
  background: #fff;
  height: calc(100vh - 383px);
  border-radius: 4px;
  border: 1px solid #eaecf0;
  padding: 20px;
}
.messIcon {
  color: rgba($color: #000000, $alpha: 0.25);
}
.content_padding {
  padding: 20px;
  // background: #f9fbfc;
}
.newButton {
  background-color: $theme-color;
  border-color: $theme-color;
}
.actionSpan {
  height: 70px;
  display: flex;
  align-items: center;
}
.formtable {
  padding: 10px 30px;
  text-align: initial;
}
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
  // width: 84%;
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
  border-radius: 4px;
  border: 1px solid #eaecf0;
  margin-bottom: 20px;
  background: #fff;
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
  width: 110px;
  padding: 0 20px;
}
.edit {
  color: #16c493;
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
  padding: 10px 0 15px 0;
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
<style lang="scss">
.tab_class {
  .el-tabs__nav-wrap::after {
    background: none;
  }
  .el-tabs__header {
    padding: 0 20px;
  }
  .el-tabs__content {
    background: #f9fbfc;
    padding: 20px;
    height: calc(100vh - 343px);
  }
}
.content_message {
  .el-form-item__label {
    text-align: left;
  }
  .el-form-item__content {
    margin-left: 0px !important;
  }
}
</style>
