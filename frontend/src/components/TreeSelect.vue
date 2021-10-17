<template>
  <div class="tree-select-wraper">
    <el-select v-model="mineStatus" :placeholder="placeholder" multiple collapse-tags @change="changeSelect" style="width: 100%">
      <el-option :value="mineStatusValue" style="height: auto">
        <el-tree
          class="tree-select-wrapers"
          :data="treeData"
          show-checkbox
          node-key="id"
          ref="treeRef"
          highlight-current
          :default-checked-keys="checkedKeys"
          :props="defaultProps"
          @check-change="handleCheckChange"
        ></el-tree>
      </el-option>
    </el-select>
  </div>
</template>
<script>
import { ref, watch, computed } from 'vue';
import { DataGranularityMap } from '@/util/constant';
// import _cloneDeep from 'lodash/cloneDeep'
export default {
  name: 'Test',
  props: {
    data: {
      /**树形数据 */
      type: Array,
      default: () => {
        return [];
      },
    },
    value: {
      /**树形选择节点id */
      type: Array,
      default: () => {
        return [];
      },
    },
    type: {
      /**判断是存储组还是实体 */
      type: String,
      default: '',
    },
  },
  setup(props, { emit }) {
    let treeRef = ref(null);
    let mineStatusValue = ref([]);
    let mineStatus = ref([]);
    // 树形
    let treeData = ref([]);
    // 平铺后的树形
    let treeList = ref([]);
    let defaultProps = {
      children: 'children',
      label: 'name',
    };
    let nameType = computed(() => {
      return {
        [DataGranularityMap.group]: '所有存储组',
        [DataGranularityMap.device]: '所有实体',
      }[props.type];
    });
    let placeholder = computed(() => {
      return {
        [DataGranularityMap.group]: '请选择存储组',
        [DataGranularityMap.device]: '请选择实体',
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
      treeRef.value.setCheckedNodes(arrNew); //设置勾选的值
    };
    const handleCheckChange = () => {
      let res = treeRef.value.getCheckedNodes(false, false); //这里两个true，1. 是否只是叶子节点 2. 是否包含半选节点（就是使得选择的时候不包含父节点）
      mineStatus.value = res.map((d) => d.name);
      mineStatusValue.value = res;
      emit('change', filterValue(mineStatus.value));
    };
    const filterValue = (origin) => {
      if (origin.includes('所有存储组') || origin.includes('所有实体')) {
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
.tree-select-wraper {
  &:deep(.el-select-dropdown__item) {
    padding: 0 !important;
  }
}
.el-select-dropdown__item {
  padding: 0 !important;
}
</style>
