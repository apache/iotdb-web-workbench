<template>
    <div class="databasem">
        <el-container class="content-container">
            <el-aside :width="dividerWidth + 'px'"><data-list-tree :nodekey="nodekey" ref="treeRef" :handleNodeClick="handleNodeClick"></data-list-tree></el-aside>
            <div class="divider" ref="dividerRef"></div>
            <el-main>
                <template v-if="urlTabs.length !== 0">
                    <el-tabs v-model="urlTabsValue" type="card" @tab-click="handleClick" @tab-remove="removeTab" closable>
                        <el-tab-pane v-for="item in urlTabs" :key="item.name" :name="item.name">
                            <template #label>
                                <span
                                    ><svg class="icon" aria-hidden="true">
                                        <use :xlink:href="urlTabsValue == item.name ? '#icon-xinzengshujulianjie-color' : '#icon-xinzengshujulianjie'"></use>
                                    </svg>
                                    {{ item.title }}</span
                                >
                            </template>
                        </el-tab-pane>
                    </el-tabs>
                    <div class="router-container">
                        <router-view></router-view>
                    </div>
                </template>
            </el-main>
        </el-container>
    </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref } from 'vue';
import useElementResize from './hooks/useElementResize.js';
import DataListTree from './components/dataListTree.vue';
import { ElContainer, ElAside, ElMain, ElTabs, ElTabPane } from 'element-plus';

export default {
    name: 'Root',
    setup() {
        const dividerRef = ref(null);
        let dividerWidth = ref(300);
        let urlTabsValue = ref('2');
        const nodekey = ref('');
        let treeRef = ref(null);
        let urlTabs = ref([
            {
                title: 'Tab 1',
                name: '1',
                content: 'Tab 1 content',
            },
            {
                title: 'Tab 2',
                name: '2',
                content: 'Tab 2 content',
            },
        ]);

        const handleClick = (tab) => {
            console.log(tab.props.name);
            console.log(treeRef.value, 'pppww');
            treeRef.value.treeRef.setCurrentKey(null);
            nodekey.value = '';
        };

        const handleNodeClick = (data) => {
            nodekey.value = data.id;
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
            nodekey,
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
    .divider {
        width: 1px;
        height: 100%;
        background-color: #f0f0f0;
        cursor: w-resize;
        &:hover {
            background-color: $theme-color !important;
            width: 2px;
        }
    }
    .router-container {
        height: calc(100% - 42px);
        width: 100%;
        overflow: auto;
    }
    &::v-deep .content-container {
        height: 100%;
        .el-aside {
            height: 100%;
        }
        .el-main {
            padding: 0;
            height: 100%;
            .el-tabs__header {
                margin: 0;
                .el-tabs__nav {
                    border: 0;
                    .el-tabs__item {
                        padding: 10px 15px;
                        box-sizing: content-box;
                        border-width: 0;
                        line-height: 22px;
                        height: 22px;
                        position: relative;
                        &.is-active {
                            background: rgba(69, 117, 246, 0.04);
                            color: $theme-color;
                        }
                    }
                }
            }
        }
    }
}
</style>
