<template>
  <div class="data-list-tree">
    <div class="data-list-top">
      <span>数据列表</span>
      <svg class="icon icon-1" aria-hidden="true" @click="btnClick1">
        <use xlink:href="#icon-xinjianchaxun"></use>
      </svg>
      <svg class="icon icon-2" aria-hidden="true" @click="btnClick2">
        <use xlink:href="#icon-xinzengshujulianjie"></use>
      </svg>
    </div>
    <div class="data-list-input">
      <el-input size="small" placeholder="" v-model="searchVal">
        <template #suffix>
          <i @click="searchClick" class="el-icon-search"></i>
        </template>
      </el-input>
    </div>
    <el-tree
      ref="treeRef"
      highlight-current
      node-key="id"
      :indent="20"
      :props="treeProps"
      @node-click="nodeClick"
      :load="loadNode"
      lazy
    >
    </el-tree>
  </div>
</template>

<script>
import { ElTree, ElInput } from "element-plus";
import { reactive, ref } from "vue";

export default {
  name: "DataListTree",
  props: ["handleNodeClick"],
  setup(props) {
    const treeProps = reactive({
      label: "name",
      children: "zones",
      isLeaf: "leaf",
    });
    const searchVal = ref("");
    const treeRef = ref(null);
    const searchClick = () => {
      console.log("jj");
    };

    const nodeClick = (data) => {
      props.handleNodeClick(data);
    };
    const btnClick1 = () => {};
    const btnClick2 = () => {};

    const loadNode = (node, resolve) => {
      if (node.level === 0) {
        return resolve([{ name: "region", id: "1" }]);
      }
      if (node.level === 2) {
        setTimeout(() => {
          const data = [
            {
              name: "leaf",
              leaf: true,
              id: "5",
            },
            {
              name: "zone",
              id: "6",
            },
          ];
          resolve(data);
        }, 500);
      }
      if (node.level > 1) return resolve([]);
      console.log("kkkkkkkkkkkk");
      setTimeout(() => {
        const data = [
          {
            name: "leaf",
            leaf: true,
            id: "2",
          },
          {
            name: "zone",
            id: "3",
          },
        ];
        resolve(data);
      }, 500);
    };

    return {
      loadNode,
      searchClick,
      nodeClick,
      btnClick1,
      btnClick2,
      treeProps,
      searchVal,
      treeRef,
    };
  },
  components: {
    ElTree,
    ElInput,
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
.data-list-tree {
  .data-list-top {
    color: rgba(34, 34, 34, 0.65);
    font-size: 12px;
    line-height: 20px;
    text-align: left;
    margin: 15px 20px;
    position: relative;
    .icon {
      font-size: 16px;
      position: absolute;
      cursor: pointer;
    }
    .icon-1 {
      top: 2px;
      right: 30px;
    }
    .icon-2 {
      top: 2px;
      right: 0;
    }
  }
  .data-list-input {
    margin: 0 20px 15px;
  }
}
</style>
