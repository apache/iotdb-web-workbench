<template>
  <div>
    <div class="headerbox">
      <div class="flexBox" style="padding: 30px 0 10px 0">
        <div class="headerSpan">{{ routeData.obj.name }}</div>
        <div class="flexBox headerIcon">
          <i class="el-icon-edit edit" @click="editDevce"></i>
          <i class="el-icon-delete delete" @click="deleteData"></i>
        </div>
      </div>
      <div class="messageBox">
        <span>{{ $t('device.dataconnection') }}：</span>
        <span>{{ routeData.obj.parentids }}</span>
        <span class="spanmargin">{{ $t('device.group') }}：</span>
        <span>{{ routeData.obj.storagegroupid }}</span>
        <span class="spanmargin">{{ $t('device.description') }}：</span>
        <span>{{ deviceObj.deviceData.description }}</span>
        <span class="spanmargin">{{ $t('device.creator') }}：</span>
        <span>{{ deviceObj.deviceData.creator }}</span>
        <span class="spanmargin">{{ $t('device.createTime') }}：</span>
        <span>{{ deviceObj.deviceData.time }}</span>
      </div>
    </div>
    <div style="padding: 20px 30px" class="flexBox">
      <form-table :form="form" @serchFormData="serchFormData"></form-table>
      <!-- <el-button type="primary" class="newButton" @click="creatDevice">{{ $t('storagePage.newDevice') }}</el-button> -->
    </div>
    <stand-table
      :column="column"
      :tableData="tableData"
      :getList="getListData"
      :total="totalCount"
      :lineHeight="5"
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
  </div>
</template>

<script>
import { ElMessageBox, ElMessage } from 'element-plus';
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
    let widths = ref(window.screen.width);
    let drawer = ref(0);
    let loading = ref(true);
    let drawerFlag = ref(false);
    let connection = ref('');
    let totalCount = ref(0);
    const pagination = reactive({
      pageSize: 10,
      pageNum: 1,
    });
    const routeData = reactive({
      obj: route.params,
    });
    const drawerRef = ref(null);
    let deviceObj = reactive({
      deviceData: 0,
    });
    const form = reactive({
      inline: true, //横向
      formData: {},
      formItem: [
        {
          label: 'device.physicalname', //名称
          type: 'INPUT', //控件类型
          size: 'small', //element尺寸
          width: '400px',
          itemID: 'keyword', //数据字段名
          suffixIcon: 'el-icon-search', // 后图标样式
          placeholder: 'device.serchPy', //灰色提示文字
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
    const tableData = reactive({
      list: [],
    });
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
      console.log(1);
      console.log(val);
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
    // ElButton,
    Echarts,
    action,
  },
};
</script>

<style lang="scss" scoped>
$cursor: pointer;
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
