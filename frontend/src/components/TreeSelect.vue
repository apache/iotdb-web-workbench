<template>
  <div class="tree-select-wraper">
    <el-select v-model="mineStatus" placeholder="请选择" multiple collapse-tags @change="selectChange" style="width: 30%">
      <el-option :value="mineStatusValue" style="height: auto">
        <el-tree
          class="tree-select-wrapers"
          :data="data"
          show-checkbox
          node-key="id"
          ref="tree"
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
import { ElSelect, ElOption, ElTree } from 'element-plus';
export default {
  name: 'Test',
  data() {
    return {
      mineStatus: this.selectArray,
      mineStatusValue: [],

      defaultProps: {
        children: 'children',
        label: 'label',
      },
    };
  },
  props: {
    data: {
      /**树形数据 */ type: Array,
      default: () => {
        return [];
      },
    },
    selectArray: {
      /**下拉框选中展示文本 */ type: Array,
      default: () => {
        return [];
      },
    },
    checkedKeys: {
      /**树形选择节点id */ type: Array,
      default: () => {
        return [];
      },
    },
  },
  computed: {},
  mounted() {},
  methods: {
    selectChange(e) {
      var arrNew = [];
      var dataLength = this.mineStatusValue.length;
      var eleng = e.length;
      for (let i = 0; i < dataLength; i++) {
        for (let j = 0; j < eleng; j++) {
          if (e[j] === this.mineStatusValue[i].label) {
            arrNew.push(this.mineStatusValue[i]);
          }
        }
      }
      this.$refs.tree.setCheckedNodes(arrNew); //设置勾选的值
    },
    handleCheckChange() {
      let res = this.$refs.tree.getCheckedNodes(false, false); //这里两个true，1. 是否只是叶子节点 2. 是否包含半选节点（就是使得选择的时候不包含父节点）
      let arrLabel = [];
      let arr = [];
      res.forEach((item) => {
        arrLabel.push(item.label);
        arr.push(item);
      });
      this.mineStatusValue = arr;
      this.mineStatus = arrLabel;
      console.log(arr);
      console.log('11111111');
      console.log(arrLabel);
      //   console.log('arr:' + JSON.stringify(arr));
      //   console.log('arrLabel:' + arrLabel);
    },
  },
  components: {
    ElSelect,
    ElOption,
    ElTree,
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
