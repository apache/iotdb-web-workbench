<template>
  <div>
    <div></div>
    <div>
      <el-input placeholder="请输入" v-model="searchVal">
        <template #suffix>
          <i @click="searchClick" class="el-icon-search"></i>
        </template>
      </el-input>
    </div>
    <el-tree
      ref="treeRef"
      highlight-current
      node-key="id"
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

    const loadNode = (node, resolve) => {
      if (node.level === 0) {
        return resolve([{ name: "region", id: "1" }]);
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
<style scoped lang="scss"></style>
