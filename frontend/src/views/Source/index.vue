<template>
  <div class="source-detail-container">
    <div class="info-box">
      <p class="title">{{ baseInfo.alias }}</p>
      <p class="more">
        <span>{{ $t('sourcePage.host') }}{{ baseInfo.host }}</span>
        <span>{{ $t('sourcePage.port') }}{{ baseInfo.port }}</span>
      </p>
    </div>
    <div class="permission-box">
      <div class="info-head">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-xinzengshujulianjie"></use>
        </svg>
        {{ $t('sourcePage.accountPermit') }}
      </div>
      <div class="permit-content">
        <div class="left-part">
          <p class="title clearfix">
            {{ $t('sourcePage.userAccount') }}
            <el-button type="text" @click="newUser()">{{ $t('sourcePage.newAccount') }}</el-button>
          </p>
          <ul class="user-list">
            <li v-for="(item, index) in userList" :class="activeIndex == index ? 'active' : ''" :key="index" @click="handleUser(index)">
              <span class="content">{{ item.name }}</span>
              <svg v-if="activeIndex == index" class="icon" aria-hidden="true" @click="deleteUser()">
                <use xlink:href="#icon-se-icon-delete"></use>
              </svg>
            </li>
          </ul>
        </div>
        <div class="right-part">
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane :label="$t('sourcePage.baseConfig')" name="1">
              <div v-if="!isNew" class="tab-content left-base-content">
                <el-form ref="baseInfoFormRef" :model="baseInfoForm" :rules="baseRules" label-position="top" class="source-form">
                  <el-form-item :label="$t('sourcePage.userNameTitle')">
                    <div>{{ baseInfoForm.userName }}</div>
                  </el-form-item>
                  <el-form-item :label="$t('sourcePage.passwordTitle')" prop="password" class="password-form-item">
                    <el-input show-password v-if="edit" v-model="baseInfoForm.password"></el-input>

                    <svg v-if="!edit" class="icon" aria-hidden="true" @click="editBaseInfo()">
                      <use xlink:href="#icon-se-icon-f-edit"></use>
                    </svg>
                    <div v-if="!edit">
                      {{ baseInfoForm.password }}
                    </div>
                    <div v-if="edit">
                      <el-button @click="cancelEdit()">{{ $t('common.cancel') }}</el-button>
                      <el-button type="primary">{{ $t('common.submit') }}</el-button>
                    </div>
                  </el-form-item>
                </el-form>
              </div>
              <div v-else class="tab-content left-base-content">
                <el-form ref="baseInfoFormRef" :model="baseInfoForm" :rules="baseRules" label-position="top" class="source-form">
                  <el-form-item prop="userName" :label="$t('sourcePage.userNameTitle')" class="userName-form-item">
                    <el-input v-model="baseInfoForm.userName"></el-input>
                  </el-form-item>
                  <el-form-item :label="$t('sourcePage.passwordTitle')" prop="password" class="password-form-item">
                    <el-input show-password v-model="baseInfoForm.password"></el-input>

                    <div>
                      <el-button @click="cancelNew()">取消</el-button>
                      <el-button type="primary">确定</el-button>
                    </div>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>
            <el-tab-pane v-if="!isNew" :label="$t('sourcePage.accountPermit')" name="2">dsdsd</el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
    <div class="storage-box">
      <div class="info-head">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-cunchuzu-color"></use>
        </svg>
        {{ $t('sourcePage.groupInfo') }}
      </div>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="groupName" :label="$t('sourcePage.groupName')" width="180"> </el-table-column>
        <el-table-column prop="description" :label="$t('sourcePage.description')"> </el-table-column>
        <el-table-column prop="ttl" :label="$t('sourcePage.line')"> </el-table-column>
        <el-table-column :label="$t('common.operation')">
          <template #default="scope">
            <!-- @click="handleClick(scope.row)" -->
            <el-button type="text" size="small">{{ $t('common.detail') }}{{ scope.row.ttl }}</el-button>
            <el-button type="text" size="small">
              {{ $t('common.edit') }}
            </el-button>
            <el-button type="text" size="small">
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-button @click="newSource()">新建数据源</el-button>
    <NewSource v-if="showDialog" :showDialog="showDialog" :types="types" @close="close()" />
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, reactive, ref } from 'vue';
import { ElButton, ElTable, ElTableColumn, ElTabs, ElTabPane, ElForm, ElFormItem, ElInput } from 'element-plus';
import NewSource from './components/newSource.vue';
import { useI18n } from 'vue-i18n';

export default {
  name: 'Source',
  setup() {
    const { t } = useI18n();
    let showDialog = ref(false);
    let types = ref(0);
    let activeName = ref('1');
    let userList = reactive([{ name: 'ewew' }, { name: 'dsdsd' }, { name: 'ewew' }, { name: 'dsdsd' }]);
    let baseInfo = reactive({
      alias: 'dsdsdsd',
      host: '127.0.0.1',
      port: 9090,
    });
    let tableData = reactive([
      {
        groupName: '2016-05-02',
        ttl: '王小虎',
        description: '上海市普陀区金沙江路 1518 弄',
      },
    ]);
    let baseInfoForm = reactive({
      userName: 'dsdsds',
      password: '123456',
    });
    const baseInfoFormRef = ref(null);
    const baseRules = reactive({
      password: [
        {
          required: true,
          message: t(`sourcePage.passwordEmptyTip`),
          trigger: 'change',
        },
      ],
      userName: [
        {
          required: true,
          message: t(`sourcePage.usernameEmptyTip`),
          trigger: 'change',
        },
      ],
    });
    let activeIndex = ref(null);
    let edit = ref(false);
    let isNew = ref(false);
    const newSource = () => {
      showDialog.value = true;
      types.value = 0;
    };
    const close = () => {
      showDialog.value = false;
      types.value = 0;
    };
    const handleClick = (tab) => {
      activeName.value = tab.paneName;
    };
    const handleUser = (index) => {
      activeIndex.value = index;
    };
    const editBaseInfo = () => {
      edit.value = true;
    };
    const cancelEdit = () => {
      edit.value = false;
    };
    const deleteUser = () => {
      console.log(1);
    };
    const newUser = () => {
      isNew.value = true;

      userList.unshift({ name: 'new' });
    };
    const cancelNew = () => {
      isNew.value = false;
    };
    onMounted(() => {});

    return {
      newSource,
      close,
      showDialog,
      types,
      baseInfo,
      tableData,
      userList,
      activeIndex,
      handleUser,
      handleClick,
      activeName,
      baseInfoForm,
      baseInfoFormRef,
      baseRules,
      edit,
      editBaseInfo,
      cancelEdit,
      deleteUser,
      newUser,
      isNew,
      cancelNew,
    };
  },
  components: {
    ElButton,
    ElTable,
    ElTableColumn,
    NewSource,
    ElTabs,
    ElTabPane,
    ElForm,
    ElFormItem,
    ElInput,
  },
};
</script>

<style scoped lang="scss">
.source-detail-container {
  height: 100%;
  text-align: left;
  &::v-deep .el-tabs__nav-wrap::after {
    display: none !important;
  }

  &::v-deep .el-tabs__header .el-tabs__nav .el-tabs__item.is-active {
    background: #fff !important;
  }
  .info-box {
    padding: 10px 20px;
  }
  .info-box {
    font-size: 16px;
    font-weight: 500;
    color: #222222;
    .title {
      margin: 10px 0 20px;
    }
    .more {
      span {
        color: rgba(34, 34, 34, 0.65);
        font-size: 14px;
        margin-right: 40px;
      }
    }
  }
  .icon {
    cursor: pointer;
  }

  .permission-box {
    .permit-content {
      display: flex;
      flex-direction: row;
      .left-part {
        width: 240px;
        margin-top: 16px;
        border-right: 1px solid #f0f0f0ff;
        .title {
          height: 17px;
          font-size: 12px;
          font-weight: 400;
          line-height: 20px;
          color: rgba(34, 34, 34, 0.65);
          padding: 0 16px 10px;
          button {
            float: right;
            font-size: 12px;
            margin-top: -7px;
          }
        }
        .user-list {
          margin-bottom: 10px;
          max-height: 250px;
          overflow: auto;
          .active {
            background: rgba(69, 117, 246, 0.04);
            color: $theme-color;
          }
          li {
            height: 36px;
            line-height: 36px;
            position: relative;
            font-size: 12px;
            padding: 0 16px;
            cursor: pointer;
            .content {
              display: inline-block;
              max-width: 150px;
              overflow: hidden;
              text-overflow: ellipsis;
            }
            .icon {
              position: absolute;
              right: 16px;
              top: 10px;
            }
          }
        }
      }
      .right-part {
        flex: 1;
        &::v-deep .el-tabs__active-bar {
          // width: 23px !important;
          // margin-left: 14px;
        }

        .tab-content {
          padding: 10px 30px;

          .password-form-item {
            position: relative;

            .icon {
              color: $theme-color;
              position: absolute;
              top: -31px;
              left: 100px;
              cursor: pointer;
            }
          }
        }
        .left-base-content {
          .el-input {
            width: 200px;
            // display: block;
            .el-input__suffix {
              top: auto;
            }
          }
        }
      }
    }
  }
  .info-head {
    height: 40px;
    line-height: 40px;
    background: #f6f6f8;
    font-size: 14px;
    color: #222222;
    padding-left: 20px;
    .icon {
      font-size: 16px;
      cursor: pointer;
      margin-right: 4px;
    }
  }
}
</style>
