<template>
  <div class="databasem">
    <el-container class="content-container">
      <el-aside :width="dividerWidth + 'px'"
        ><data-list-tree
          ref="treeRef"
          :handleNodeClick="handleNodeClick"
        ></data-list-tree
      ></el-aside>
      <div class="divider" ref="dividerRef"></div>
      <el-main>
        <el-tabs
          v-model="urlTabsValue"
          type="card"
          @tab-click="handleClick"
          @tab-remove="removeTab"
          closable
        >
          <el-tab-pane
            v-for="item in urlTabs"
            :key="item.name"
            :label="item.title"
            :name="item.name"
          >
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref } from "vue";
import useElementResize from "./hooks/useElementResize.js";
import DataListTree from "./components/dataListTree.vue";
import { ElContainer, ElAside, ElMain, ElTabs, ElTabPane } from "element-plus";

export default {
  name: "Root",
  setup() {
    const dividerRef = ref(null);
    let dividerWidth = ref(300);
    let urlTabsValue = ref("2");
    let treeRef = ref(null);
    let urlTabs = ref([
      {
        title: "Tab 1",
        name: "1",
        content: "Tab 1 content",
      },
      {
        title: "Tab 2",
        name: "2",
        content: "Tab 2 content",
      },
    ]);

    const handleClick = (tab) => {
      console.log(tab.props.name);
      console.log(treeRef.value, "pppww");
      treeRef.value.treeRef.setCurrentKey(null);
    };

    const handleNodeClick = (data) => {
      console.log(data);
    };

    const removeTab = (targetName) => {
      let tabs = urlTabs.value;
      let activeName = urlTabsValue.value;
      if (activeName === targetName) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            let nextTab = tabs[index + 1] || tabs[index - 1];
            if (nextTab) {
              activeName = nextTab.name;
            }
          }
        });
      }
      urlTabsValue.value = activeName;
      urlTabs.value = tabs.filter((tab) => tab.name !== targetName);
    };

    onMounted(() => {
      useElementResize(dividerRef, dividerWidth);
    });

    return {
      urlTabsValue,
      dividerRef,
      dividerWidth,
      urlTabs,
      treeRef,
      handleClick,
      removeTab,
      handleNodeClick,
    };
  },
  components: {
    ElContainer,
    ElAside,
    ElMain,
    DataListTree,
    ElTabs,
    ElTabPane,
  },
};
</script>

<style scoped lang="scss">
.databasem {
  height: 100%;
  &::v-deep .content-container {
    height: 100%;
    .divider {
      width: 1px;
      height: 100%;
      background-color: #f0f0f0;
      cursor: w-resize;
      &:hover {
        background-color: $theme-color;
      }
    }
  }
}
</style>
