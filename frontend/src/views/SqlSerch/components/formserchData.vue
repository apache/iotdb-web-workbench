<template>
  <div>
    <div class="serch_div">
      <el-input v-model="filterText" :placeholder="placeholder" class="elinput" suffix-icon="el-icon-search"></el-input>
    </div>
    <div class="serch_div maxheight">
      <el-tree :data="data.list" :props="defaultProps" :load="loadNode" lazy @check-change="handleCheckChange" ref="tree">
        <template #default="{ node, data }">
          <span class="custom-tree-node chil" v-if="data.table" @dblclick="getFunction(node)">
            <span>{{ node.label }}</span>
            <span>{{ data.decr || '--' }}</span>
            <span>{{ data.type }}</span>
          </span>
          <span v-else>{{ node.label }}</span>
        </template>
      </el-tree>
    </div>
  </div>
</template>

<script>
import { ElInput, ElTree } from 'element-plus';
import { ref, reactive } from 'vue';
import { getDevice, getCList } from '../api/index';
export default {
  props: {
    placeholder: String,
    treeList: Object,
    id: Number,
  },
  setup(props, { emit }) {
    const data = reactive(props.treeList);
    let filterText = ref(null);
    const tree = ref(null);
    const defaultProps = {
      children: 'children',
      label: 'label',
    };
    function append(val) {
      console.log(val);
    }
    function getFunction(val) {
      console.log(val);
      emit('getFunction', `root.${val.data.parents}.${val.data.parent}.${val.data.label}`);
    }
    function loadNode(node, solve) {
      if (node.data.deciceF) {
        console.log(node);
        getCList(props.id, node.data.parent, node.data.value, { pageSize: 99999, pageNum: 1 }).then((res) => {
          const data = res.data.measurementVOList.map((item) => {
            return {
              parents: node.data.parent,
              parent: node.data.value,
              label: item.timeseries,
              type: item.dataType,
              decr: item.description,
              table: true,
              children: res.data.measurementVOList,
            };
          });
          data.unshift({
            label: '测点名称',
            type: '类型',
            decr: '描述',
            table: true,
          });
          return solve(data);
        });
      } else {
        getDevice(props.id, node.data.value).then((res) => {
          const data = res.data.map((item) => {
            return {
              parent: node.data.value,
              label: item,
              value: item,
              decr: '',
              type: '',
              deciceF: true,
            };
          });
          return solve(data);
        });
      }
    }
    return { data, defaultProps, filterText, tree, append, getFunction, loadNode };
  },
  components: {
    ElInput,
    ElTree,
  },
};
</script>

<style lang="scss" scoped>
.serch_div {
  padding: 20px 20px 0px 20px;
  background: #fff;
  &.maxheight {
    height: 75vh;
    overflow: auto;
  }
}
.elinput {
  height: 30px;
  line-height: 30px;
}
</style>
<style lang="scss">
.serch_div .el-tree-node__content {
  height: 35px !important;
}
.elinput {
  .el-input__inner {
    height: 30px;
    line-height: 30px;
  }
  .el-input__icon {
    line-height: 30px;
  }
}
.custom-tree-node.chil {
  width: 115%;
  margin-left: -25%;
  display: flex;
  font-size: 14px;
  justify-content: space-between;
  border-bottom: 1px solid #ebeef5;
  padding: 5px 0px 5px 0px;
  span {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    text-align: left;
  }
}
</style>
