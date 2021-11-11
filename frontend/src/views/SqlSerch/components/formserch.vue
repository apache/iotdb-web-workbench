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
    <div class="serch_div">
      <el-input v-model="filterText" :placeholder="$t(placeholder)" class="elinputs" suffix-icon="el-icon-search"></el-input>
    </div>
    <div class="serch_div maxheight">
      <el-tree :data="data" :props="defaultProps" accordion @node-click="handleNodeClick" :filter-node-method="filterNode" ref="tree">
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <eltooltip :label="data.value">
              <span @dblclick="getFunction(node.label)">{{ $t(node.label) }}</span>
            </eltooltip>
          </span>
        </template>
      </el-tree>
    </div>
  </div>
</template>

<script>
import { ElInput, ElTree } from 'element-plus';
import eltooltip from './eltooltip';
import list from '../hooks/function';
import { watch, ref } from 'vue';
export default {
  props: {
    placeholder: String,
  },
  setup(props, { emit }) {
    console.log(props);
    const data = list;
    let filterText = ref('');
    const tree = ref(null);
    const defaultProps = {
      children: 'children',
      label: 'label',
    };
    watch(filterText, (newValue) => {
      tree.value.filter(newValue.toLocaleUpperCase());
    });
    function append(val) {
      console.log(val);
    }
    function getFunction(val) {
      if (val.indexOf('.') === -1) {
        emit('getFunction', val);
      }
    }
    function filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    }
    return { data, defaultProps, filterText, tree, append, getFunction, filterNode };
  },
  components: {
    ElInput,
    ElTree,
    eltooltip,
  },
};
</script>

<style lang="scss" scoped>
.serch_div {
  font-size: 14px;
  padding: 20px 20px 0 20px;
  background: #fff;
  &.maxheight {
    height: calc(100vh - 289px);
    overflow: auto;
  }
}
.elinputs {
  height: 30px;
  line-height: 30px;
}
</style>
<style lang="scss">
.elinputs {
  .el-input__inner {
    height: 30px;
    line-height: 30px;
  }
  .el-input__icon {
    line-height: 30px;
  }
}
</style>
