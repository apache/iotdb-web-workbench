<template>
  <div>
    <div class="serch_div">
      <el-input v-model="filterText" :placeholder="placeholder" class="elinput" suffix-icon="el-icon-search"></el-input>
    </div>
    <div class="serch_div">
      <el-tree :data="data" :props="defaultProps" accordion @node-click="handleNodeClick" :filter-node-method="filterNode" ref="tree">
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <eltooltip :label="data.value">
              <span @click="getFunction(node.label)">{{ $t(node.label) }}</span>
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
    let filterText = ref(null);
    const tree = ref(null);
    const defaultProps = {
      children: 'children',
      label: 'label',
    };
    watch(
      () => filterText,
      (val) => {
        console.log(val);
        console.log(tree);
        tree.value.filter(val);
      }
    );
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
  padding: 20px 20px 0px 20px;
  background: #fff;
}
.elinput {
  height: 30px;
  line-height: 30px;
}
</style>
<style lang="scss">
.elinput {
  .el-input__inner {
    height: 30px;
    line-height: 30px;
  }
  .el-input__icon {
    line-height: 30px;
  }
}
</style>
