<template>
  <div class="about">
    <h1>This is an about page</h1>
  </div>
</template>
<script>
import { onUpdated, onMounted } from 'vue';
export default {
  name: 'About',
  props: ['data', 'func'],
  setup(props) {
    onMounted(() => {
      console.log(props.func, 'yyy');
    });
    onUpdated(() => {
      // console.log(props.data, 'yyy');
    });
    return {};
  },
  components: {},
};
</script>
