<template>
  <div class="standTable">
    <div class="border_table">
      <el-table
        :data="tableDatas.list"
        style="width: 100%"
        :max-height="maxHeight"
        :height="Height"
        tooltip-effect="light"
        :cell-style="{
          padding: `${celineHeight ? celineHeight : 0}px ${celineWidth ? celineWidth : 0}px !important`,
        }"
        :header-cell-style="{
          color: 'black',
          overflow: 'hidden',
          background: '#F9FAFC',
          padding: `${lineHeight ? lineHeight : 0}px ${lineWidth ? lineWidth : 0}px !important`,
        }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column v-if="selectData" type="selection" width="50" align="center"> </el-table-column>
        <el-table-column :key="item.prop" v-for="item of columns.list" :width="item.width + 'px'" :align="item.align" :fixed="item.fixed" show-overflow-tooltip>
          <template #header>
            <span :class="{ spanbox: item.required }"></span>
            <span>{{ $t(item.label) }}</span>
            <i :class="item.icon" style="margin-left: 4px" @click="iconEvent"></i>
          </template>
          <template #default="scope">
            <el-input
              v-if="item.type === 'INPUT' && (!scope.row[item.prop] || scope.row.display)"
              v-model="scope.row[item.prop]"
              :size="item.size"
              :class="{ borderRed: (scope.row.namecopy || !scope.row[item.prop]) && scope.row.border }"
              :placeholder="$t(item.label)"
              @blur="item.event(scope, scope.row, scope.row[item.prop], $event)"
            >
            </el-input>
            <el-input
              v-if="item.type === 'TEXT'"
              v-model="scope.row[item.prop]"
              :maxlength="item.maxlength"
              :size="item.size"
              :placeholder="$t(item.label)"
              @blur="checkInput(scope.row[item.prop], item.required)"
            >
            </el-input>
            <el-select
              v-model="scope.row[item.prop]"
              :class="{ borderRed: !scope.row[item.prop] && scope.row.seBorder }"
              :placeholder="$t(item.label)"
              v-if="item.type === 'SELECT' && (!scope.row[item.prop] || scope.row.display)"
              :size="item.size"
              @change="item.event(scope, scope.row)"
            >
              <el-option v-for="item in item.options" :key="item.value" :label="item.label" :value="item.value" @click="selectEncoding(item.value, scope.row)">
                <span style="float: left">{{ item.label }}</span>
              </el-option>
            </el-select>
            <el-select
              v-model="scope.row[item.prop]"
              :class="{ borderRed: !scope.row[item.prop] && scope.row.seBorder }"
              :placeholder="$t(item.label)"
              v-if="item.type === 'SELECTCH' && (!scope.row[item.prop] || scope.row.display)"
              :size="item.size"
            >
              <el-option v-for="item in scope.row.options" :key="item.value" :label="item.label" :value="item.value">
                <span style="float: left">{{ item.label }}</span>
              </el-option>
            </el-select>
            <div v-if="item.type === 'TAG'">
              <i class="el-icon-edit editF" @click="editTag(scope.row[item.prop], scope.row.timeseries, item.prop)"></i>
              <span v-for="(item, index) in scope.row[item.prop]" :key="index"> {{ item[0] }}, </span>
            </div>
            <span v-if="item.type && scope.row[item.prop] && !scope.row.display && item.type !== 'TEXT'">{{ scope.row[item.prop] }}</span>
            <span v-if="!item.type">{{ scope.row[item.prop] || item.value }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="actionO" :label="$t(actionO.label)" :align="actionO.align">
          <template #default="scope">
            <slot :scope="scope"></slot>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="paination" v-if="paginations || deleteArry">
      <el-button v-if="deleteArry" @click="deleteArrys">{{ $t('standTable.deleteArry') }}</el-button>
      <div></div>
      <el-pagination
        v-if="paginations"
        @size-change="handleSizeChange"
        @current-change="getList"
        v-model:currentPage="paginations.pageNum"
        :page-size="10"
        :page-count="5"
        layout="total, prev, pager, next"
        :total="total"
        :hide-on-single-page="true"
      >
      </el-pagination>
    </div>
    <el-dialog :title="`${edData.label}编辑`" v-model="dialogFormVisible.flag" width="500px" class="dialog_tag">
      <div class="tag_content">
        <div>
          <span>物理量名称：</span><span>{{ edData.name }}</span>
        </div>
        <div>
          <span>物理量{{ edData.label }}：</span>
          <span class="icon_color" @click="addData(edData.data)"><i class="el-icon-circle-plus" /></span>
        </div>
        <div class="content">
          <div v-for="(item, index) in edData.data" :key="index">
            <el-input v-model="item[0]" size="small" /> = <el-input v-model="item[1]" size="small" /><el-button type="text"><i class="el-icon-delete" /></el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible.flag = false">取 消</el-button>
          <el-button type="primary" @click="dialogFormVisible.flag = false">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElTable, ElTableColumn, ElInput, ElSelect, ElOption, ElMessage, ElButton, ElPagination, ElDialog } from 'element-plus';
import { onMounted, reactive } from 'vue';
export default {
  name: 'StandTable',
  props: {
    column: Array,
    tableData: Array,
    selectData: Function,
    maxHeight: Number,
    Height: Number,
    lineHeight: Number,
    lineWidth: Number,
    pagination: Object,
    deleteArry: Function,
    encoding: Object,
    total: Number,
    getList: Function,
    celineWidth: Number,
    celineHeight: Number,
    backColor: String,
  },
  setup(props, { emit }) {
    let dialogFormVisible = reactive({
      flag: false,
    });
    let edData = reactive({
      data: [],
      name: '',
      label: '',
    });
    const columns = reactive({
      list: {},
    });
    const actionA = props.column.list.filter((item) => item.prop === 'action');
    columns.list = props.column.list.filter((item) => item.prop !== 'action');
    const actionO = actionA.length > 0 ? reactive(actionA[0]) : '';
    const paginations = reactive(props.pagination);
    const tableDatas = reactive(props.tableData);
    const encodings = reactive(props.encoding);
    function handleSelectionChange(val) {
      props.selectData(val);
    }
    function deleteArrys() {
      props.deleteArry();
    }
    function checkInput(val, flag) {
      if (!val && flag) {
        ElMessage({
          showClose: true,
          message: '该项值不能为空，请填写',
          type: 'error',
        });
      }
    }
    function getlist(val) {
      console.log(11111);
      console.log(val);
    }
    function handleCurrentChange(val) {
      console.log(val);
      emit('getPagintions', val);
    }
    function getColumn(data) {
      columns.list = data.filter((item) => item.prop !== 'action');
    }
    function selectEncoding(val, row) {
      row.options = encodings[val];
      if (!row.options) {
        row.options = encodings.DEFAULT;
      }
      row.encoding = row.options[0].value;
    }
    function iconEvent() {
      emit('iconEvent');
    }
    function editTag(arr, name, str) {
      let obj = {
        tags: '标签',
        attributes: '属性',
      };
      edData.data = arr;
      edData.name = name;
      edData.label = obj[str];
      dialogFormVisible.flag = true;
    }
    function addData(arr) {
      arr.unshift([null, null]);
    }
    onMounted(() => {
      console.log(columns);
    });
    return {
      addData,
      deleteArrys,
      edData,
      columns,
      actionO,
      paginations,
      tableDatas,
      checkInput,
      handleSelectionChange,
      handleCurrentChange,
      selectEncoding,
      getlist,
      iconEvent,
      getColumn,
      editTag,
      dialogFormVisible,
    };
  },
  components: {
    ElTable,
    ElTableColumn,
    ElInput,
    ElSelect,
    ElOption,
    ElButton,
    ElPagination,
    ElDialog,
  },
};
</script>

<style lang="scss" scoped>
.border_table {
  border-radius: 4px;
  border: 1px solid #eaecf0;
}
.editF {
  cursor: pointer;
  padding-right: 3px;
}
.tag_content {
  div {
    padding: 8px 0px;
  }
  .icon_color {
    cursor: pointer;
    color: #7a859f;
  }
  .content {
    div {
      text-align: center;
    }
    i {
      color: red;
    }
    .el-input__inner {
      width: 150px;
    }
  }
}
.spanbox::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}
.borderRed .el-input__inner {
  border: 1px solid red;
}
.paination {
  display: flex;
  justify-content: space-between;
  padding: 10px 0px;
  .el-pagination {
    padding: 4px 5px 0px 5px;
  }
}
.export_button {
  height: 30px;
  line-height: 0px;
  min-height: 0px !important;
}
</style>
<style lang="scss">
.borderRed {
  .el-input__inner {
    border: 1px solid red;
  }
}
.standTable {
  .el-dialog__header {
    border-bottom: 1px solid #eef0f5;
  }
}
.tag_content {
  .content {
    .el-input {
      width: 100px;
      padding: 0px 16px;
    }
  }
}
</style>
