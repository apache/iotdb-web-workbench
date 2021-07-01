<template>
  <div>
    <div class="serch_div">
      <el-input
        v-model="input"
        :placeholder="placeholder"
        class="elinput"
        suffix-icon="el-icon-search"
      ></el-input>
    </div>
    <div class="serch_div">
      <el-tree
        :data="data"
        :props="defaultProps"
        accordion
        @node-click="handleNodeClick"
      >
      </el-tree>
    </div>
  </div>
</template>

<script>
import { ElInput, ElTree } from "element-plus";
export default {
  props: {
    placeholder: String,
  },
  setup() {
    const data = [
      {
        label: "一级 1",
        children: [
          {
            label: "二级 1-1",
            children: [
              {
                label: "三级 1-1-1",
              },
            ],
          },
        ],
      },
      {
        label: "一级 2",
        children: [
          {
            label: "二级 2-1",
            children: [
              {
                label: "三级 2-1-1",
              },
            ],
          },
          {
            label: "二级 2-2",
            children: [
              {
                label: "三级 2-2-1",
              },
            ],
          },
        ],
      },
      {
        label: "一级 3",
        children: [
          {
            label: "二级 3-1",
            children: [
              {
                label: "三级 3-1-1",
              },
            ],
          },
          {
            label: "二级 3-2",
            children: [
              {
                label: "三级 3-2-1",
              },
            ],
          },
        ],
      },
    ];
    const defaultProps = {
      children: "children",
      label: "label",
    };
    return { data, defaultProps };
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
