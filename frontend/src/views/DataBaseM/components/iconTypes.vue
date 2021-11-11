<!--
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
-->

<template>
  <span class="iconTypes">
    <span v-if="data.type === 'connection'" class="custom-tree-node-icon"
      ><svg class="icon" aria-hidden="true">
        <use :xlink:href="nodekey == data.id ? '#icon-shujulianjie1' : '#icon-shujulianjie1'"></use></svg
    ></span>
    <span v-if="data.type === 'newstorageGroup'" class="custom-tree-node-icon"
      ><svg class="icon" aria-hidden="true">
        <use :xlink:href="nodekey == data.id ? '#icon-add1' : '#icon-add1'"></use></svg
    ></span>
    <span v-if="data.type === 'storageGroup'" class="custom-tree-node-icon"
      ><svg class="icon" aria-hidden="true">
        <use :xlink:href="nodekey == data.id ? '#icon-cunchuzu1' : '#icon-cunchuzu1'"></use></svg
    ></span>
    <span v-if="data.type === 'querylist'" class="custom-tree-node-icon"
      ><svg class="icon" aria-hidden="true">
        <use :xlink:href="nodekey == data.id ? '#icon-chaxun1' : '#icon-chaxun1'"></use></svg
    ></span>
    <span v-if="data.type === 'newquery'" class="custom-tree-node-icon"
      ><svg class="icon" aria-hidden="true">
        <use :xlink:href="nodekey == data.id ? '#icon-add1' : '#icon-add1'"></use></svg
    ></span>
    <span v-if="data.type === 'query'" class="custom-tree-node-icon"
      ><svg class="icon" aria-hidden="true">
        <use :xlink:href="nodekey == data.id ? '#icon-chaxun1' : '#icon-chaxun1'"></use></svg
    ></span>
    <span v-if="data.type === 'newdevice'" class="custom-tree-node-icon"
      ><svg class="icon" aria-hidden="true">
        <use :xlink:href="nodekey == data.id ? '#icon-add1' : '#icon-add1'"></use></svg
    ></span>
    <span v-if="data.type === 'device'" class="custom-tree-node-icon"
      ><svg class="icon" aria-hidden="true">
        <use :xlink:href="nodekey == data.id ? '#icon-shiti' : '#icon-shiti'"></use></svg
    ></span>
  </span>
</template>

<script>
export default {
  name: 'IconTypes',
  props: ['data', 'nodekey'],
  setup() {},
  components: {},
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
.custom-tree-node-icon {
  margin-right: 8px;
}
</style>
