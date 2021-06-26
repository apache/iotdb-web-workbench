<template>
  <div class="data-list-tree">
    <div class="data-list-top">
      <span>{{ $t("rootPage.dataList") }}</span>
      <el-tooltip
        :content="$t('rootPage.newQueryWindow')"
        :visible-arrow="false"
        effect="light"
      >
        <div class="icon-1">
          <svg
            class="icon"
            aria-hidden="true"
            @click="btnClick1"
            v-icon="`#icon-xinjianchaxun-color`"
          >
            <use xlink:href="#icon-xinjianchaxun"></use>
          </svg>
        </div>
      </el-tooltip>
      <el-tooltip
        :content="$t('rootPage.newdatasource')"
        :visible-arrow="false"
        effect="light"
      >
        <div class="icon-2">
          <svg
            v-icon="`#icon-xinzengshujulianjie-color`"
            class="icon"
            aria-hidden="true"
            @click="btnClick2"
            title="1212"
          >
            <use xlink:href="#icon-xinzengshujulianjie"></use>
          </svg>
        </div>
      </el-tooltip>
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
import { ElTree, ElInput, ElTooltip } from "element-plus";
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
      if (node.level === 3) {
        setTimeout(() => {
          const data = [
            {
              name: "leaf",
              id: "8",
            },
            {
              name: "leaf",
              id: "17",
            },
            {
              name: "leafdsssssssssssssssssssssssssssskkkk",
              id: "16",
            },
            {
              name: "leaf",
              id: "15",
            },
            {
              name: "leaf",
              id: "13",
            },
            {
              name: "leaf",
              leaf: true,
              id: "18",
            },
            {
              name: "zone",
              id: "9",
            },
          ];
          resolve(data);
        }, 500);
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
    ElTooltip,
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
.data-list-tree {
  height: 100%;
  overflow: auto;
  .data-list-top {
    color: rgba(34, 34, 34, 0.65);
    font-size: 12px;
    line-height: 20px;
    text-align: left;
    margin: 15px 20px;
    position: relative;
    .icon {
      font-size: 16px;
      cursor: pointer;
    }
    .icon-1 {
      top: 2px;
      right: 0px;
      position: absolute;
    }
    .icon-2 {
      top: 2px;
      right: 30px;
      position: absolute;
    }
  }
  .data-list-input {
    margin: 0 20px 15px;
  }
  &::v-deep .el-tree {
    height: calc(100% - 97px);
    width: 100%;
    overflow: auto;
    .el-tree-node {
      min-width: fit-content;
    }
    .el-tree-node__content {
      min-width: fit-content;
    }
  }
}
</style>
