<template>
  <div style="padding: 20px">
    <form-table :form="form"></form-table>
    <div class="addbox">
      {{ $t('device.physical') }}
      <el-button type="primary" class="addbutton" size="small">
        {{ $t('device.addphysical') }}
      </el-button>
    </div>
    <div class="tableBox">
      <stand-table :column="column" :tableData="tableData" :selectData="selectData" :lineHeight="5" :maxHeight="250">
        <template #default="{ scope }">
          <el-button @click="deleteRow(scope.row)" type="text" size="small" style="color: red">
            {{ $t('device.delete') }}
          </el-button>
        </template>
      </stand-table>
    </div>
    <div class="footer">
      <el-button type="info">{{ $t('device.cencel') }}</el-button>
      <el-button type="primary">{{ $t('device.ok') }}</el-button>
    </div>
  </div>
</template>

<script>
import FormTable from '@/components/FormTable';
import StandTable from '@/components/StandTable';
import { ElButton } from 'element-plus';
import { onMounted, reactive } from 'vue';
import { getDeviceDate } from './api';
export default {
  name: 'DeviceAddEidt',
  setup() {
    const column = reactive([
      {
        label: 'device.physicalname',
        prop: 'name',
        type: 'INPUT', //控件类型
        width: 350,
        required: true, //必填标志
        size: 'small',
      },
      {
        label: 'device.datatype',
        prop: 'type',
        type: 'SELECT',
        width: 200,
        required: true,
        size: 'small',
      },
      {
        label: 'device.codingmode',
        prop: 'func',
        type: 'SELECT',
        width: 200,
        required: true,
        size: 'small',
        icon: 'el-icon-question',
      },
      {
        label: 'device.physicaldescr',
        prop: 'mess',
        type: 'TEXT',
        width: 700,
        size: 'small',
      },
      {
        label: 'device.action',
        prop: 'action',
        align: 'center',
      },
    ]);
    const tableData = [
      {
        name: 'data_test',
        mess: '1234214',
      },
      {
        name: 'data_test',
      },
      {
        name: 'data_test',
      },
    ];
    const form = reactive({
      // inline: true, //横向
      labelPosition: 'left', //文本对齐方式
      formData: {
        name: 'data',
        mess: '描述',
        date: '1997.12.10',
        save: 'xxxxx/thjt_test',
      },
      formItem: [
        {
          label: 'device.devicename', //名称
          type: 'INPUT', //控件类型
          size: 'small', //element尺寸
          labelWidth: '40%', //块宽度 px 或 %
          itemID: 'name', //数据字段名
          placeholder: '请输入设备名称', //灰色提示文字
          required: true, //是否必填
          message: '请输入设备名称', //报错提示信息
        },
        {
          label: 'device.description', //名称
          type: 'INPUT', //控件类型
          size: 'small', //尺寸
          labelWidth: '40%', //块宽度 px 或 %
          // width: "200px", //表单控件宽度 px 或 %
          itemID: 'mess', //数据字段名
          placeholder: '请输入设备描述', //灰色提示文字
          required: false, //是否必填
          message: '请输入设备描述', //报错提示信息
        },
        {
          label: 'device.group', //名称
          type: 'TEXT', //控件类型
          size: 'small', //尺寸
          labelWidth: '40%', //块宽度 px 或 %
          // width: "200px", //表单控件宽度 px 或 %
          itemID: 'save', //数据字段名
          placeholder: '', //灰色提示文字
          required: false, //是否必填
          message: '', //报错提示信息
        },
      ],
    });
    function deleteRow(a) {
      console.log(a);
      console.log(a.name);
    }
    onMounted(() => {
      getDeviceDate(9, 'mytest', 'test').then((res) => {
        console.log(res);
      });
      console.log(111);
    });
    return {
      deleteRow,
      form,
      tableData,
      column,
    };
  },
  components: {
    FormTable,
    ElButton,
    StandTable,
  },
};
</script>

<style lang="scss" scoped>
.addbox {
  font-size: 14px;
  text-align: left;
  color: #606266;
}
.addbutton {
  color: #ffffff;
  margin-left: 20px;
  padding: 10px 30px;
  background-color: #0080ff;
  border-color: #0080ff;
  line-height: 0px;
}
.addbutton:hover {
  background-color: #3a3dff;
}
.tableBox {
  margin-top: 20px;
  border: 1px solid #ebeef5;
}
.footer {
  position: absolute;
  bottom: 10px;
  left: 50%;
  // text-align: center;
}
</style>
