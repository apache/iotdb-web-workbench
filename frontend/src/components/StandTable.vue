<!--
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
-->

<template>
  <div>
    <el-table
      :data="tableDatas.list"
      style="width: 100%"
      :max-height="maxHeight"
      :height="Height"
      tooltip-effect="light"
      :cell-style="{
        padding: `${lineHeight ? lineHeight : 0}px ${celineWidth ? celineWidth : 0}px !important`,
      }"
      :header-cell-style="{
        color: 'black',
        overflow: 'hidden',
        padding: `${lineHeight ? lineHeight : 0}px ${lineWidth ? lineWidth : 0}px !important`,
      }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="selectData" type="selection" width="80" align="center"> </el-table-column>
      <el-table-column :key="item.prop" v-for="item of columns.list" :width="item.width + 'px'" :align="item.align" show-overflow-tooltip>
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
    <div class="paination" v-if="paginations || exportData">
      <el-button v-if="exportData" class="export_button">{{ $t('standTable.export') }}</el-button>
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
  </div>
</template>

<script>
import { ElTable, ElTableColumn, ElInput, ElSelect, ElOption, ElMessage, ElButton, ElPagination } from 'element-plus';
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
    exportData: Function,
    encoding: Object,
    total: Number,
    getList: Function,
    celineWidth: Number,
  },
  setup(props, { emit }) {
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
    onMounted(() => {
      console.log(1234);
      console.log(columns);
    });
    return {
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
.borderRed .el-input__inner {
  border: 1px solid red;
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
<style lang="scss">
.borderRed {
  .el-input__inner {
    border: 1px solid red;
  }
}
</style>
