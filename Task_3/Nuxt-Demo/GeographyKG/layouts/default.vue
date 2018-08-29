<template>
  <div>
    <div class="header">
      <top-nav/>
    </div>

    <nuxt class="main-content"/>

    <div class="footer">
      <bottom-footer/>
    </div>
  </div>
</template>

<script>
  import TopNav from "~/components/TopNav.vue";
  import BottomFooter from "~/components/BottomFooter.vue";
  export default {
    components: {TopNav, BottomFooter}
  }
</script>

<style>
  .header {
    flex: 0 0 auto;
    height: 60px;
  }
  .main-content {
    flex: 1 0 auto;
  }
  .footer {
    flex: 0 0 auto;
  }
</style>
