<template>
  <router-view />
</template>

<style lang="scss">
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #222222;
  font-family: PingFang SC, Arial, sans-serif;
  text-align: center;
  height: 100vh;
  position: relative;
  .icon {
    width: 1em;
    height: 1em;
    vertical-align: -0.15em;
    fill: currentColor;
    overflow: hidden;
  }
}
// 滚动条开始
::-webkit-scrollbar-track-piece {
  background-color: #fff;
}
::-webkit-scrollbar {
  width: 4px;
  height: 6px;
  background-color: #fff;
}
::-webkit-scrollbar-thumb {
  height: 20%;
  background-color: rgba(193, 193, 193, 0.5);
  border-radius: 6px;
  outline-offset: -2px;
  filter: alpha(opacity = 50);
  opacity: 0.5;
}
::-webkit-scrollbar-corner {
  background-color: #fff;
}
//滚动条完
.clearfix {
  zoom: 1;
}
.clearfix::after {
  content: '';
  display: block;
  height: 0;
  clear: both;
  visibility: hidden;
}
.move-resize {
  cursor: w-resize !important;
  user-select: none;
  .el-tree,
  .el-tree-node__content,
  li {
    cursor: w-resize !important;
  }
  .divider {
    background-color: $theme-color !important;
    width: 2px !important;
  }
}
</style>
