<template>
  <div>
    <el-table
      :data="tableDatas"
      style="width: 100%"
      :max-height="maxHeight"
      :height="Height"
      tooltip-effect="light"
      :cell-style="{
        padding: `${lineHeight ? lineHeight : 0}px ${lineWidth ? lineWidth : 0}px`,
      }"
      :header-cell-style="{
        color: 'black',
        padding: `${lineHeight ? lineHeight : 0}px ${lineWidth ? lineWidth : 0}px`,
      }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="selectData" type="selection" width="80" align="center"> </el-table-column>
      <el-table-column :key="item.prop" v-for="item of columns" :width="item.width + 'px'" :align="item.align" show-overflow-tooltip>
        <template #header>
          <span :class="{ spanbox: item.required }"></span>
          <span>{{ $t(item.label) }}</span>
          <i :class="item.icon" style="margin-left: 4px"></i>
        </template>
        <template #default="scope">
          <el-input
            v-if="item.type === 'INPUT' && !scope.row[item.prop]"
            v-model="scope.row[item.prop]"
            :size="item.size"
            :placeholder="$t(item.label)"
            @blur="checkInput(scope.row[item.prop], item.required)"
          >
          </el-input>
          <el-input v-if="item.type === 'TEXT'" v-model="scope.row[item.prop]" :size="item.size" :placeholder="$t(item.label)" @blur="checkInput(scope.row[item.prop], item.required)"> </el-input>
          <el-select v-model="scope.row[item.prop]" :placeholder="$t(item.label)" v-if="item.type === 'SELECT' && !scope.row[item.prop]" :size="item.size">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
              <span style="float: left">{{ item.label }}</span>
            </el-option>
          </el-select>
          <span v-if="item.type && scope.row[item.prop] && item.type !== 'TEXT'">{{ scope.row[item.prop] }}</span>
          <span v-if="!item.type">{{ scope.row[item.prop] || item.value }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="actionO" :label="$t(actionO.label)" :align="actionO.align">
        <template #default="scope">
          <slot :scope="scope"></slot>
        </template>
      </el-table-column>
    </el-table>
    <div class="paination" v-if="paginations || exportData">
      <el-button v-if="exportData" class="export_button">批量导出</el-button>
      <el-pagination
        v-if="paginations"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        v-model:currentPage="paginations.currentPage"
        :page-size="10"
        :page-count="5"
        layout="total, prev, pager, next"
        :total="paginations.total"
        :hide-on-single-page="true"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { ElTable, ElTableColumn, ElInput, ElSelect, ElOption, ElMessage, ElButton, ElPagination } from 'element-plus';
import { reactive } from 'vue';
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
    exportData: Function,
  },
  setup(props) {
    const actionA = props.column.filter((item) => item.prop === 'action');
    const columns = props.column.filter((item) => item.prop !== 'action');
    const actionO = actionA.length > 0 ? reactive(actionA[0]) : '';
    const tableDatas = reactive(props.tableData);
    const paginations = reactive(props.pagination);
    function handleSelectionChange(val) {
      props.selectData(val);
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
    function handleCurrentChange(val) {
      console.log(val);
    }
    return {
      columns,
      tableDatas,
      actionO,
      paginations,
      checkInput,
      handleSelectionChange,
      handleCurrentChange,
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
  },
};
</script>

<style lang="scss" scoped>
.spanbox::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}
.paination {
  display: flex;
  justify-content: space-between;
  padding: 10px 30px;
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