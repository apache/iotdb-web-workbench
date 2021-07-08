<template>
  <div class="about">
    <h1>This is an about page</h1>
  </div>
</template>
<script>
import { onUpdated, onMounted, onActivated } from 'vue';
import { useRoute } from 'vue-router';
export default {
  name: 'About',
  props: ['data', 'func'],
  setup() {
    const route = useRoute();
    onMounted(() => {
      console.log('kkkkyyy');
    });
    onUpdated(() => {
      // console.log(props.data, 'yyy');
    });

    onActivated(() => {
      if (route.params.forceupdate) {
        console.log(route.params, 'update');
      }
    });
    return {};
  },
  components: {},
};
</script>
