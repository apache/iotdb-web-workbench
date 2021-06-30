<template>
    <div class="storage-container">
        <div class="base-info">
            <div class="btns">
                <svg class="icon" aria-hidden="true" @click="editGroup()">
                    <use xlink:href="#icon-se-icon-f-edit"></use>
                </svg>
                <svg class="icon" aria-hidden="true">
                    <use xlink:href="#icon-se-icon-delete"></use>
                </svg>
            </div>
            <el-descriptions :title="baseInfo.groupName">
                <el-descriptions-item :label="$t('storagePage.alias') + ':'">{{
                    baseInfo.alias
                }}</el-descriptions-item>
                <el-descriptions-item
                    :label="$t('storagePage.creator') + ':'"
                    >{{ baseInfo.creator }}</el-descriptions-item
                >
                <el-descriptions-item :label="$t('storagePage.createTime')">{{
                    baseInfo.createTime
                }}</el-descriptions-item>
                <el-descriptions-item :label="$t('storagePage.ttl')">
                    {{ baseInfo.ttl }} {{ baseInfo.ttlUnit }}
                </el-descriptions-item>
                <el-descriptions-item
                    :label="$t('storagePage.description') + ':'"
                    >江苏省苏州市吴中区吴中大道 1188 号</el-descriptions-item
                >
            </el-descriptions>
        </div>
        <div class="device-content">
            <div class="search-panel clearfix">
                <span class="search-title">{{
                    $t("storagePage.deviceName")
                }}</span>
                <el-input
                    v-model="searchVal"
                    suffix-icon="el-icon-search"
                ></el-input>
                <el-button type="primary" class="search-btn">{{
                    $t("storagePage.newDevice")
                }}</el-button>
            </div>
            <div class="device-list">
                <el-table
                    :data="tableData"
                    style="width: 100%"
                    @selection-change="handleSelectionChange"
                >
                    <el-table-column type="selection" width="55">
                    </el-table-column>
                    <el-table-column
                        show-overflow-tooltip
                        prop="deviceName"
                        :label="$t('storagePage.alias')"
                        width="180"
                        sortable
                    >
                    </el-table-column>
                    <el-table-column
                        show-overflow-tooltip
                        prop="description"
                        :label="$t('storagePage.description')"
                    >
                    </el-table-column>
                    <el-table-column
                        prop="line"
                        :label="$t('storagePage.line')"
                    >
                    </el-table-column>
                    <el-table-column
                        prop="creator"
                        :label="$t('storagePage.creator')"
                    >
                    </el-table-column>
                    <el-table-column :label="$t('storagePage.operation')">
                        <template #default="scope">
                            <!-- @click="handleClick(scope.row)" -->

                            <el-button type="text" size="small"
                                >{{ $t("common.edit")
                                }}{{ scope.row.ttl }}</el-button
                            >
                            <el-button
                                type="text"
                                size="small"
                                class="el-button-delete"
                                >{{ $t("common.delete") }}</el-button
                            >
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination
                    @current-change="handleCurrentChange"
                    :currentPage="currentPage"
                    :page-size="pageSize"
                    layout="total, prev, pager, next"
                    :total="total"
                >
                </el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, reactive, ref } from "vue";
import {
    ElDescriptions,
    ElDescriptionsItem,
    ElInput,
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
} from "element-plus";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

export default {
    name: "Storage",
    setup() {
        const { t } = useI18n();
        const router = useRouter();

        let baseInfo = reactive({
            groupName: "dsdsdsddsdsdsd",
            creator: "dsds",
            createTime: "dsdsd",
            ttl: "34",
            ttlUnit: "秒",
            description: "ewewewewe",
            alias: "ewewewewewewewewewewewewewewewew",
        });
        let searchVal = ref(null);
        let tableData = reactive([
            { deviceName: "1s", creator: "ds", line: "ds" },
            { deviceName: "2s", creator: "ds", line: "ds" },
            { deviceName: "3s", creator: "ds", line: "ds" },
            { deviceName: "4s", creator: "ds", line: "ds" },
        ]);
        let currentPage = ref(1);
        const pageSize = ref(15);
        let total = ref(0);
        const handleSelectionChange = (selection) => {
            console.log(selection);
        };
        const handleCurrentChange = (val) => {
            console.log(val);
        };
        const editGroup = () => {
            router.push({
                name: "NewStorage",
                params: { serverid: router.currentRoute.value.params.serverid },
            });
        };
        onMounted(() => {});

        return {
            t,
            baseInfo,
            searchVal,
            tableData,
            currentPage,
            pageSize,
            total,
            handleSelectionChange,
            handleCurrentChange,
            editGroup,
        };
    },
    components: {
        ElDescriptions,
        ElDescriptionsItem,
        ElInput,
        ElButton,
        ElTable,
        ElTableColumn,
        ElPagination,
    },
};
</script>

<style scoped lang="scss">
.storage-container {
    height: 100%;
    text-align: left;
    .base-info,
    .device-content {
        padding: 20px;
    }
    .base-info {
        border-bottom: 1px solid #f0f0f0;
        position: relative;
        .btns {
            position: absolute;
            right: 20px;
            .icon {
                cursor: pointer;
            }
            .icon:first-child {
                margin-right: 20px;
                color: $theme-color;
            }
            .icon:last-child {
                margin-right: 20px;
                color: #d32d2fff;
            }
        }
    }
    .device-content {
        .search-panel {
            .search-title {
                font-size: 14px;
            }
            .el-input {
                width: 300px;
                margin-left: 10px;
            }
            .search-btn {
                float: right;
            }
        }
        .el-pagination {
            text-align: right;
            margin-top: 16px;
        }
    }
}
</style>
