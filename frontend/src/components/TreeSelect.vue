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
  <div class="tree-select-wraper">
    <el-select v-model="mineStatus" :placeholder="placeholder" multiple collapse-tags @change="changeSelect" style="width: 100%">
      <el-option :value="mineStatusValue" style="height: auto">
        <el-tree class="tree-select-wrapers" :data="treeData" show-checkbox node-key="name" ref="treeRef" highlight-current :props="defaultProps" @check-change="handleCheckChange"></el-tree>
      </el-option>
    </el-select>
  </div>
</template>
<script>
import { ref, watch, computed } from 'vue';
import { DataGranularityMap } from '@/util/constant';
import { useI18n } from 'vue-i18n';
// import _cloneDeep from 'lodash/cloneDeep'
export default {
  name: 'Test',
  props: {
    data: {
      /** Tree data */
      type: Array,
      default: () => {
        return [];
      },
    },
    checkedKeys: {
      /** Tree selection node ID */
      type: Array,
      default: () => {
        return [];
      },
    },
    type: {
      /** Determine storage group or entity */
      type: String,
      default: '',
    },
  },
  setup(props, { emit }) {
    const { t } = useI18n();
    let treeRef = ref(null);
    let mineStatusValue = ref([]);
    let mineStatus = ref([]);
    let treeData = ref([]);
    let treeList = ref([]);
    let defaultProps = {
      children: 'children',
      label: 'name',
    };
    let nameType = computed(() => {
      return {
        [DataGranularityMap.group]: t('sourcePage.allStorageGroups'),
        [DataGranularityMap.device]: t('sourcePage.allDevices'),
      }[props.type];
    });
    let placeholder = computed(() => {
      return {
        [DataGranularityMap.group]: t('device.selectdataconnection'),
        [DataGranularityMap.device]: t('device.selectp'),
      }[props.type];
    });
    const changeSelect = (e) => {
      let arrNew = [];
      let dataLength = mineStatusValue.value.length;
      let eleng = e.length;
      for (let i = 0; i < dataLength; i++) {
        for (let j = 0; j < eleng; j++) {
          if (e[j] === mineStatusValue.value[i].label) {
            arrNew.push(mineStatusValue.value[i]);
          }
        }
      }
      treeRef.value.setCheckedNodes(arrNew); //Set the checked value
    };
    const handleCheckChange = () => {
      //There are two truths here: 1. Whether it is only a leaf node; 2. Whether it contains a semi selected node (that is, it does not include a parent node when selecting)
      let res = treeRef.value.getCheckedNodes(false, false);
      mineStatus.value = res.map((d) => d.name);
      mineStatusValue.value = res;
      emit('change', filterValue(mineStatus.value));
    };
    const filterValue = (origin) => {
      if (origin.includes(t('sourcePage.allStorageGroups')) || origin.includes(t('sourcePage.allDevices'))) {
        return treeList.value.filter((d) => d.level === 2).map((i) => i.name);
      }
      origin.forEach((d) => {
        let obj = treeList.value.find((i) => i.name === d);
        if (obj) {
          let childrenNames = obj.children.map((i) => i.name);
          origin = origin.filter((i) => !childrenNames.includes(i));
        }
      });
      return origin;
    };

    const loopDirectory = (root) => {
      const list = [root];
      const loop = (node) => {
        node.children = node.children || [];

        node.children.forEach((d) => {
          d.level = node.level + 1;
          d.path = [
            ...node.path,
            {
              name: d,
            },
          ];
          list.push(d);
          loop(d);
        });
      };
      loop(root);
      return list;
    };
    /**
     * All child nodes of the given node
     * */
    const loopNode = (directory) => {
      let res = [];
      res.push(directory.name);
      if (directory && directory.children) {
        directory.children.forEach((i) => {
          res.push(...loopNode(i));
        });
      }
      return res;
    };
    watch(
      () => props.data,
      (data) => {
        let tree = {
          name: nameType.value,
          level: 1,
          path: [
            {
              id: '-1',
              name: nameType.value,
            },
          ],
          children: data,
        };
        treeData.value = [tree];
        treeList.value = loopDirectory({ ...tree });
      }
    );
    watch(
      () => props.checkedKeys,
      (val) => {
        mineStatus.value = [];
        mineStatusValue.value = [];
        val.forEach((e) => {
          let obj = treeList.value.find((d) => d.name === e);
          mineStatus.value = mineStatus.value.concat(loopNode(obj));
          mineStatus.value.forEach((d) => {
            let obj = treeList.value.find((e) => e.name === d);
            obj && mineStatusValue.value.push(obj);
          });
          treeRef.value.setCheckedNodes(mineStatusValue.value);
        });
      }
    );
    return {
      mineStatus,
      mineStatusValue,
      defaultProps,
      changeSelect,
      handleCheckChange,
      treeRef,
      treeData,
      filterValue,
      placeholder,
    };
  },
};
</script>
<style lang="scss" scoped>
:deep(.el-tree-node__content) {
  height: 30px;
}
:deep(.el-tree--highlight-current) {
  & .el-tree-node {
    & .el-tree-node__content {
      border-width: 0;
      font-size: 12px !important;
    }
  }
}
.tree-select-wraper {
  &:deep(.el-select-dropdown__item) {
    padding: 0 !important;
  }
}
.el-select-dropdown__item {
  padding: 0 !important;
}
</style>
