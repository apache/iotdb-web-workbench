<template>
  <div class="empty-page">
    <div class="img"></div>
    <p>
      {{ $t('rootPage.nodatasource') }}<el-button type="text" @click="newSource()">{{ $t('rootPage.newdatasource') }}</el-button>
    </p>
    <NewSource v-if="showDialog" :func="func" :serverId="null" :showDialog="showDialog" :types="types" @close="close()" @successFunc="successFunc(data)" />
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref } from 'vue';
import { ElButton } from 'element-plus';
import { useI18n } from 'vue-i18n';
// import axios from '@/util/axios.js';
// import { useStore } from 'vuex';
// import { useRoute } from 'vue-router';
import NewSource from './newSource.vue';

export default {
  name: 'Empty',
  props: ['func'],
  setup(props) {
    const { t } = useI18n();
    // const router = useRoute();
    let showDialog = ref(false);
    let types = ref(0);

    /**
     * 新增数据连接
     */
    const newSource = () => {
      showDialog.value = true;
      types.value = 0;
    };
    /**
     * 关闭或者取消新增/编辑数据连接操作
     */
    const close = () => {
      showDialog.value = false;
      types.value = 0;
    };
    /**
     * 新增或编辑数据源成功回调
     */
    const successFunc = () => {
      showDialog.value = false;
      types.value = 0;
      props.func.removeTab(props.data.id);
    };
    onMounted(() => {});

    return {
      showDialog,
      types,
      newSource,
      successFunc,
      close,
      t,
    };
  },
  components: {
    ElButton,
    NewSource,
  },
};
</script>

<style scoped lang="scss">
.empty-page {
  margin-top: 20vh;
  .img {
    background: url(~@/assets/empty.png) no-repeat;
    background-size: contain;
    width: 180px;
    height: 136px;
    margin: 0 auto;
  }
}
</style>
