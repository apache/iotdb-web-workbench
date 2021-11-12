<template>
  <table v-if="treeData && treeData.partnerName">
    <tr>
      <td :colspan="treeData.childers ? treeData.childers.length * 2 : 1" :class="{ parentLevel: treeData.childers, extend: treeData.childers && treeData.childers.length && treeData.extend }">
        <div :class="{ node: true, hasMate: treeData.mate }">
          <div class="person" @click="$emit('click-node', treeData)">
            <el-popover placement="top" width="180" trigger="hover">
              <div style="margin: 0">
                <el-button size="mini" type="primary" @click="addStock(0)" v-if="treeData.partnerType !== 1 && treeData.partnerType !== 3">添加</el-button>
                <el-button type="primary" size="mini" @click="addStock(1)" v-if="treeData.proportionShares">编辑</el-button>
                <el-button type="primary" size="mini" @click="deleteStock" v-if="treeData.proportionShares">删除</el-button>
              </div>
              <template v-slot:reference>
                <div class="avat" :class="{ parent: !treeData.proportionShares, company: Number(treeData.partnerType) === 2, other: Number(treeData.partnerType) === 3 }">
                  {{ treeData.partnerName }}({{ treeData.proportionShares ? treeData.proportionShares : 100 }}%)
                </div>
              </template>
            </el-popover>
            <div class="avat" :class="{ parent: !treeData.proportionShares, company: Number(treeData.partnerType) === 2, other: Number(treeData.partnerType) === 3 }">
              {{ treeData.partnerName }}({{ treeData.proportionShares }}%)
            </div>
          </div>
        </div>
        <div class="extend_handle" v-if="treeData.childers && treeData.childers.length" @click="toggleExtend(treeData)"></div>
      </td>
    </tr>
    <!-- 这是一个递归组件,注意,这里还要调用,需要传递的数据这里也要传递,否则操作时拿不到子级的数据 -->
    <tr v-if="treeData.childers && treeData.childers.length && treeData.extend">
      <td v-for="(childers, index) in treeData.childers" :key="index" colspan="2" class="childLevel">
        <TreeChart :json="childers" @add="$emit('add', $event)" @delete="$emit('delete', $event)" @click-node="$emit('click-node', $event)" />
      </td>
    </tr>
  </table>
</template>

<script>
export default {
  name: 'TreeChart',
  props: {
    json: {}, // 渲染数据
  },

  data() {
    return {
      treeData: {},
    };
  },

  created() {
    // console.log(this.json)
  },

  watch: {
    json: {
      // 遍历当前的数据
      handler: function (Props) {
        let extendKey = function (jsonData) {
          jsonData.extend = jsonData.extend === void 0 ? true : !!jsonData.extend;
          // if (Array.isArray(jsonData.children) && jsonData.children.length) {
          //   jsonData.children.forEach(c => {
          //     extendKey(c);
          //   });
          // }
          return jsonData;
        };
        if (Props) {
          this.treeData = extendKey(Props);
        }
      },
      immediate: true,
      deep: true,
    },
  },
  methods: {
    toggleExtend(treeData) {
      treeData.extend = !treeData.extend;
      this.$forceUpdate();
    },

    // 新增编辑股东,val: 0 新增, 1 编辑
    addStock(val) {
      // console.log(this.treeData)
      this.$emit('add', { val: val, data: this.treeData });
    },

    // 删除股东
    deleteStock() {
      this.$emit('delete', this.treeData);
    },
  },
};
</script>

<style lang="scss">
table {
  border-collapse: separate !important;
  border-spacing: 0 !important;
}
td {
  position: relative;
  vertical-align: top;
  padding: 0 0 50px 0;
  text-align: center;
}

.parent {
  background: #199ed8 !important;
  font-weight: bold;
}
.extend_handle {
  position: absolute;
  left: 50%;
  bottom: 27px;
  width: 10px;
  height: 10px;
  padding: 10px;
  transform: translate3d(-15px, 0, 0);
  cursor: pointer;
}
.extend_handle::before {
  content: '';
  display: block;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  border: 2px solid;
  border-color: #ccc #ccc transparent transparent;
  transform: rotateZ(135deg);
  transform-origin: 50% 50% 0;
  transition: transform ease 300ms;
}
.extend_handle:hover::before {
  border-color: #333 #333 transparent transparent;
}
.extend .extend_handle::before {
  transform: rotateZ(-45deg);
}

.extend::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 15px;
  height: 15px;
  border-left: 2px solid #ccc;
  transform: translate3d(-1px, 0, 0);
}
.childLevel::before {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 100%;
  height: 15px;
  border-left: 2px solid #ccc;
  transform: translate3d(-1px, 0, 0);
}
.childLevel::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: -15px;
  border-top: 2px solid #ccc;
}
.childLevel:first-child::before,
.childLevel:last-child::before {
  display: none;
}
.childLevel:first-child::after {
  left: 50%;
  height: 15px;
  border: 2px solid;
  border-color: #ccc transparent transparent #ccc;
  border-radius: 6px 0 0 0;
  transform: translate3d(1px, 0, 0);
}
.childLevel:last-child::after {
  right: 50%;
  height: 15px;
  border: 2px solid;
  border-color: #ccc #ccc transparent transparent;
  border-radius: 0 6px 0 0;
  transform: translate3d(-1px, 0, 0);
}
.childLevel:first-child.childLevel:last-child::after {
  left: auto;
  border-radius: 0;
  border-color: transparent #ccc transparent transparent;
  transform: translate3d(1px, 0, 0);
  display: none;
}

.node {
  position: relative;
  display: inline-block;
  box-sizing: border-box;
  text-align: center;
  padding: 0 5px;
}
.node .person {
  // padding-top: 15px;
  position: relative;
  display: inline-block;
  z-index: 2;
  width: 120px;
  overflow: hidden;
}
.node .person .avat {
  padding: 5px;
  padding-top: 10px;
  display: block;
  width: 100%;
  height: 100%;
  margin: auto;
  word-break: break-all;
  background: #fc0;
  box-sizing: border-box;
  border-radius: 4px;
  .opreate_icon {
    display: none;
  }
  &:hover {
    .opreate_icon {
      display: block;
      position: absolute;
      top: -3px;
      right: -3px;
      padding: 5px;
    }
  }

  &.company {
    background: #199ed8;
  }
  &.other {
    background: #ccc;
  }
}
.node .person .avat img {
  cursor: pointer;
}
.node .person .name {
  height: 2em;
  line-height: 2em;
  overflow: hidden;
  width: 100%;
}
.node.hasMate::after {
  content: '';
  position: absolute;
  left: 2em;
  right: 2em;
  top: 15px;
  border-top: 2px solid #ccc;
  z-index: 1;
}
// 竖向
.landscape {
  transform: translate(-100%, 0) rotate(-90deg);
  transform-origin: 100% 0;
  .node {
    text-align: left;
    height: 8em;
    width: 8em;
  }
  .person {
    position: relative;
    transform: rotate(90deg);
    // padding-left: 4.5em;
    // height: 4em;
    top: 35px;
    left: 12px;
    width: 110px;
  }
}
.node.hasMate .person:last-child {
  margin-left: 1em;
}

.el-dialog__header {
  padding: 0;
  padding-top: 30px;
  margin: 0 30px;
  border-bottom: 1px solid #f1f1f1;
  text-align: left;
  .el-dialog__title {
    font-size: 14px;
    font-weight: bold;
    color: #464c5b;
    line-height: 20px;
  }
}
.tips {
  padding: 0 20px;
  .el-select {
    width: 100%;
  }
  .blue {
    color: #00b5ef;
  }
  .check {
    margin-left: 100px;
  }
  .inquiry {
    font-weight: bold;
  }
  .el-form-item__label {
    display: block;
    float: none;
    text-align: left;
  }
  .el-form-item__content {
    margin-left: 0;
  }
}
.el-dialog__body {
  padding: 30px 25px;
  p {
    margin-bottom: 15px;
  }
}
.el-dialog__headerbtn {
  top: 30px;
  right: 30px;
}

.el-popover {
  .el-button {
    padding: 8px !important;
    margin-left: 5px !important;
    float: left;
  }
}
</style>
