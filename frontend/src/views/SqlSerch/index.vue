<template>
  <el-container>
    <el-container>
      <el-header class="sqlheader">
        <div class="title flex">
          <div>
            <span>数据连接：</span>
            <span>存储组：</span>
          </div>
          <div class="rightIcon flex">
            <eltooltip label="保存">
              <span>
                <svg class="icon icon-1" aria-hidden="true" @click="btnClick1" v-icon="`#icon-baocun-color`">
                  <use xlink:href="#icon-baocun"></use>
                </svg>
              </span>
            </eltooltip>
            <eltooltip label="运行">
              <span>
                <svg class="icon icon-1" aria-hidden="true" @click="btnClick1" v-icon="`#icon-yunhang-color`">
                  <use xlink:href="#icon-yunhang"></use>
                </svg>
              </span>
            </eltooltip>
          </div>
        </div>
      </el-header>
      <el-main class="backcolor">
        <div :style="{ height: `calc(100vh - ${143 + divwerHeight}px)` }">
          <codemirror ref="codemirror" :codes="code" :sqlheight="sqlheight" @getCode="getCode"></codemirror>
        </div>
      </el-main>
      <el-footer class="footer" :style="{ display: divwerHeight > 30 ? '' : 'none' }">
        <div class="divider" ref="dividerRef"></div>
        <div :style="{ height: divwerHeight + 'px', overflow: 'auto' }">
          <div class="tabs">
            <el-tabs v-model="activeName" @tab-click="handleClick" class="tabs_nav">
              <el-tab-pane label="运行结果1" name="first">
                <template #label>
                  <span>运行结果1<i class="el-icon-more iconmore green"></i> </span>
                </template>
                <div class="header_messge flex">
                  <div>
                    <span>
                      <svg class="icon icon-1 icon-color" aria-hidden="true" @click="btnClick1">
                        <use xlink:href="#icon-se-icon-download"></use>
                      </svg>
                      <span class="downloadchart">下载</span>
                    </span>
                    <span class="frist_span">最多下载10万条数据</span>
                  </div>
                  <div>
                    <span class="frist_span">查询时间：</span>
                    <span class="frist_span">查询行数：</span>
                  </div>
                </div>
                <div class="tab_table">
                  <stand-table :column="column" :tableData="tableData" :lineHeight="5" :lineWidth="13" :maxHeight="400" :pagination="pagination"> </stand-table>
                </div>
              </el-tab-pane>
              <el-tab-pane label="运行结果2" name="second">
                <template #label>
                  <span>运行结果2<i class="el-icon-more iconmore red"></i> </span>
                </template>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </el-footer>
    </el-container>
    <el-aside width="300px">
      <div class="el_aside_div">
        <div class="tabgad">
          <el-tabs v-model="activeName" @tab-click="handleClick" class="tabs_nav_aside">
            <el-tab-pane label="函数" name="first">
              <formserch :placeholder="'请输入函数名称'"></formserch>
            </el-tab-pane>
            <el-tab-pane label="数据" name="second"></el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-aside>
  </el-container>
</template>

<script>
import { ElContainer, ElMain, ElAside, ElHeader, ElFooter, ElTabs, ElTabPane } from 'element-plus';
import StandTable from '@/components/StandTable';
import formserch from './components/formserch';
import useElementResize from './hooks/useElementResize.js';
import codemirror from './components/codemirror';
import eltooltip from './components/eltooltip';
import { onMounted, ref, computed, nextTick } from 'vue';
export default {
  name: 'Sqlserch',
  setup() {
    let divwerHeight = ref(0);
    let dividerRef = ref(null);
    let codemirror = ref(null);
    let activeName = ref('first');
    let sqlheight = computed(() => {
      nextTick(() => {
        codemirror.value.codemrriorHeight(divwerHeight.value);
      });
      return divwerHeight.value;
    });
    let code = 'select from 1';
    const column = [
      {
        label: '测点名称',
        prop: 'name',
        value: '——', //默认值，该项如果没有数据显示
      },
      {
        label: '数据类型',
        prop: 'type',
        value: '——', //默认值，该项如果没有数据显示
      },
      {
        label: '编码方式',
        prop: 'func',
        value: '——', //默认值，该项如果没有数据显示
      },
      {
        label: '测点描述',
        prop: 'mess',
        value: '——', //默认值，该项如果没有数据显示
      },
      {
        label: '最新值',
        prop: 'num',
        value: '——', //默认值，该项如果没有数据显示
      },
    ];
    const tableData = [
      {
        name: 'A111',
        type: 'string',
        func: '',
        mess: '设备',
      },
      {
        name: 'A111',
        type: 'string',
        func: 'utf',
        mess: '设备',
      },
      {
        name: 'A111',
        type: 'string',
        func: 'utf',
        mess: '设备',
      },
      {
        name: 'A222',
        type: 'string',
        func: 'utf',
        mess: '设备',
      },
      {
        name: 'A222',
        type: 'string',
        func: 'utf',
        mess: '设备',
      },
      {
        name: 'A222',
        type: 'string',
        func: 'utf',
        mess: '设备',
      },
      {
        name: 'A222',
        type: 'string',
        func: 'utf',
        mess: '设备',
      },
      {
        name: 'A222',
        type: 'string',
        func: 'utf',
        mess: '设备',
      },
      {
        name: 'A222',
        type: 'string',
        func: 'utf',
        mess: '设备',
      },
      {
        name: 'A222',
        type: 'string',
        func: 'utf',
        mess: '设备',
      },
    ];
    function getCode(val) {
      // console.log(typeof val);
      code = val;
      // console.log(code);
    }
    function btnClick1() {
      divwerHeight.value = 300;
      useElementResize(dividerRef, divwerHeight);
      codemirror.value.onCmCodeChange('插入函数');
    }
    onMounted(() => {
      useElementResize(dividerRef, divwerHeight);
    });
    return {
      column,
      tableData,
      code,
      btnClick1,
      getCode,
      codemirror,
      divwerHeight,
      dividerRef,
      sqlheight,
      activeName,
    };
  },
  components: {
    eltooltip,
    codemirror,
    ElContainer,
    ElMain,
    ElAside,
    ElHeader,
    ElFooter,
    ElTabs,
    ElTabPane,
    StandTable,
    formserch,
  },
};
</script>

<style lang="scss" scoped>
.el_aside_div {
  width: 298px;
  height: calc(100vh - 106px);
  // overflow: hidden;
  position: absolute;
  border-left: 1px solid #ebeef5;
  .tabgad {
    background: #efefef;
  }
}
.downloadchart {
  font-size: 11px;
  color: $theme-color;
  margin-left: 5px;
  cursor: pointer;
}
.sqlheader {
  &.el-header {
    height: 35px !important;
    padding: 0;
  }
}
.footer {
  &.el-footer {
    padding: 0;
    height: 0px !important;
  }
  .divider {
    // width: 1px;
    height: 1px;
    background-color: #f0f0f0;
    cursor: n-resize;
    &:hover {
      background-color: $theme-color !important;
      height: 2px;
    }
  }
  .tabs {
    height: 30px;
    background: #efefef;
    box-shadow: 0px 0px 2px #d2d2d2;
    .frist_span {
      color: #cccccc;
      font-size: 11px;
      margin-left: 20px;
    }
    .header_messge {
      padding: 10px 23px;
    }
    .tab_table {
      padding-top: 5px;
    }
  }
}
.title {
  padding: 10px 25px;
  color: black;
  font-size: 14px;
  border-bottom: 1px solid #ebeef5;
}
.rightIcon {
  width: 40px;
}
.flex {
  display: flex;
  justify-content: space-between;
}
.container {
  position: relative;
}
</style>
<style lang="scss">
.backcolor.el-main {
  padding: 0;
  height: 100%;
}
.tabs_nav .el-tabs__item {
  padding: 5px 0px !important;
  width: 100px;
  font-size: 11px !important;
}
.tabs_nav_aside .el-tabs__item {
  padding: 7px 0px !important;
  width: 60px;
  font-size: 11px !important;
}
.tabs_nav .iconmore {
  font-size: 25px;
  width: 10px;
  overflow: hidden;
  line-height: 15px;
  position: absolute;
  top: 0px;
  &.green {
    color: #00c300;
  }
  &.red {
    color: rgb(255, 33, 33);
  }
}
.icon.icon-color {
  color: $theme-color;
  vertical-align: -0.2em !important;
}
</style>
