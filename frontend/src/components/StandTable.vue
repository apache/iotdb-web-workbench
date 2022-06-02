/* * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements. See the NOTICE file * distributed with this work for additional information * regarding
copyright ownership. The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License. You may obtain a
copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, * software distributed under the License is distributed on an * "AS
IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY * KIND, either express or implied. See the License for the * specific language governing permissions and limitations * under the License. */

<template>
  <div class="standTable">
    <div class="border_table">
      <el-table
        :data="tableDatas.list"
        style="width: 100%"
        :height="Height"
        :max-height="maxHeight"
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
        <el-table-column fixed="left" v-if="selectData" type="selection" width="50" align="center"> </el-table-column>
        <el-table-column :key="item.prop" v-for="item of columns.list" min-width="180px" :width="item.width + 'px'" :align="item.align" :fixed="item.fixed" show-overflow-tooltip>
          <template #header>
            <span :class="{ spanbox: item.required }"></span>
            <svg v-if="iconArr.icon[item.icon]" :class="['icon', { 'icon-time': item.icon === 'TIME' }]" @click="sqlClick" aria-hidden="true">
              <use :xlink:href="`#icon-${iconArr.icon[item.icon]}`"></use>
            </svg>
            <span :style="{ 'margin-left': iconArr.icon[item.icon] ? '5px' : '' }" :class="[{ closable: !!item.closable }]" :title="$t(item.label)"
              >{{ $t(item.label) }}
              <span class="el-icon-close" v-on:click="item.closable(item)"></span>
            </span>
            <i :class="item.icon" style="margin-left: 4px" @click="iconEvent(item.iconNum)"></i>
          </template>
          <template #default="scope">
            <el-input
              v-if="item.type === 'INPUT' && (!scope.row[item.prop] || scope.row.display || item.canEdit)"
              v-model="scope.row[item.prop]"
              :size="item.size"
              :class="{ borderRed: (scope.row.namecopy || !scope.row[item.prop]) && scope.row.border && item.border }"
              :placeholder="$t('device.inputTip') + $t(item.label)"
              @blur="item.event(scope, scope.row, scope.row[item.prop], $event, item)"
            >
            </el-input>
            <el-input
              v-if="item.type === 'TEXT'"
              v-model="scope.row[item.prop]"
              :maxlength="item.maxlength"
              :size="item.size"
              :placeholder="$t('device.inputTip') + $t(item.label)"
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
              :placeholder="$t('device.selectTip')"
              v-if="item.type === 'SELECTCH' && (!scope.row[item.prop] || scope.row.display)"
              :size="item.size"
            >
              <el-option v-for="item in scope.row.options" :key="item.value" :label="item.label" :value="item.value">
                <span style="float: left">{{ item.label }}</span>
              </el-option>
            </el-select>
            <!-- <div v-if="item.type === 'TAGS'">
              <span v-for="(item, index) in scope.row[item.prop]" :key="index"> {{ item }}={{ item[1] }}, </span>
            </div> -->
            <div v-if="item.type === 'ATTRIBUTES' || item.type === 'TAG'">
              <i v-if="!item.onlyShow" class="el-icon-edit editF" @click="editTag(scope.row[item.prop], scope.row.timeseries, item.prop, scope.$index)"></i>
              <span v-for="(item, index) in scope.row[item.prop]" :key="index">{{ item[0] }} = {{ item[1] }}, </span>
            </div>
            <span
              :class="item.type"
              v-if="item.type && scope.row[item.prop] && !scope.row.display && item.type !== 'TEXT' && item.type !== 'ATTRIBUTES' && item.type !== 'TAG' && item.prop !== 'alias'"
              >{{ scope.row[item.prop] }}</span
            >
            <span class="item.type" v-if="!item.type">{{ scope.row[item.prop] || item.value }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" v-if="actionO" :label="$t(actionO.label)" min-width="150px">
          <template #default="scope">
            <slot :scope="scope"></slot>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="paination" v-if="paginations || deleteArry">
      <el-button v-if="deleteArry" type="primary" @click="deleteArrys">{{ $t('standTable.deleteArry') }}</el-button>
      <div></div>
      <el-pagination
        v-if="paginations"
        @size-change="handleSizeChange"
        @current-change="getList"
        v-model:currentPage="paginations.pageNum"
        :page-size="paginations.pageSize"
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
          <svg class="icon" aria-hidden="true" @click="addData(edData.data)">
            <use xlink:href="#icon-add1"></use>
          </svg>
        </div>
        <div class="content">
          <div v-for="(item, index) in edData.data" :key="index">
            <el-input v-model="item[0]" size="small" maxlength="30" :placeholder="`${edData.label}名`" /> =
            <el-input v-model="item[1]" size="small" maxlength="30" :placeholder="`${edData.label}值`" /><el-button type="text"
              ><i class="el-icon-delete" @click="deleTag(edData.data, index)"
            /></el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible.flag = false">取 消</el-button>
          <el-button type="primary" @click="confirmTag">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElTable, ElTableColumn, ElInput, ElSelect, ElOption, ElMessage, ElButton, ElPagination, ElDialog } from 'element-plus';
import { onActivated, reactive } from 'vue';
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
    const iconArr = reactive({
      icon: {
        INT64: 'int64',
        BOOLEAN: 'buer',
        INT32: 'int32',
        TEXT: 'TEXT',
        DOUBLE: 'DOUBLE',
        FLOAT: 'FLOAT',
        TIME: 'time',
      },
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
      if (val === 'FLOAT' || val === 'DOUBLE') {
        row.encoding = row.options[row.options.length - 1].value;
      } else {
        row.encoding = row.options[0].value;
      }
    }
    function iconEvent(iconNum) {
      emit('iconEvent', iconNum);
    }
    function editTag(arr, name, str, index) {
      let obj = {
        tags: '标签',
        attributes: '属性',
      };
      edData.data = [...arr];
      edData.name = name;
      edData.label = obj[str];
      edData.index = index;
      dialogFormVisible.flag = true;
    }
    function addData(arr) {
      arr.unshift([null, null]);
    }
    function deleTag(arr, index) {
      arr.splice(index, 1);
    }
    const isRepeat = (arr) => {
      var hash = {};

      for (var i in arr) {
        if (hash[arr[i]]) return true;

        hash[arr[i]] = true;
      }

      return false;
    };

    const confirmTag = () => {
      let result = isRepeat(edData.data.map((d) => d[0]));
      let keys = edData.data.map((d) => d[0]);
      let values = edData.data.map((d) => d[1]);

      let pattern = /^[0-9]*$/;
      if (keys.every((d) => pattern.test(d)) || values.every((d) => pattern.test(d))) {
        ElMessage.error(`${edData.label}不能完全为数值`);
        return;
      }
      if (edData.data.length && (!keys.every((d) => d !== null) || !values.find((d) => d !== null))) {
        ElMessage.error(`请填写完整`);
        return;
      }
      if (result) {
        ElMessage.error(`${edData.label}名不能重复`);
        return;
      }
      if (edData.label === '标签') {
        tableDatas.list[edData.index].tags = edData.data;
      } else {
        tableDatas.list[edData.index].attributes = edData.data;
      }
      dialogFormVisible.flag = false;
    };
    onActivated(() => {
      //
    });
    return {
      iconArr,
      deleTag,
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
      confirmTag,
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
.standTable {
  .icon {
    cursor: pointer;
  }
  .icon-time {
    color: #4eb5ff;
  }
}
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
    padding: 8px 0;
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
.paination {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;

  // padding: 10px 0px;
  .el-pagination {
    padding: 4px 5px 0 5px;
  }
}
.export_button {
  height: 30px;
  line-height: 0px;
  min-height: 0 !important;
}
.el-icon-close {
  display: none;
  position: absolute;
  top: 5px;
  cursor: pointer;
}
.closable {
  &:hover > .el-icon-close {
    display: inline-block;
    position: absolute;
    top: 5px;
    cursor: pointer;
  }
}
:deep(.el-table) th > .cell {
  white-space: nowrap;
}
</style>
<style lang="scss">
.borderRed {
  .el-input__inner {
    border: 1px solid red;
  }
}
.tag_content {
  .content {
    .el-input {
      width: 100px;
      padding: 0 16px;
    }
  }
}
</style>
