<template>
  <div class="container">
    <sparql-query @listenQueryButton="getQueryString" class="left-container" />
    <echarts-view class="right-container" />
  </div>
</template>

<script>
import SparqlQuery from '~/components/SparqlQuery.vue'
import EchartsView from '~/components/EchartsView.vue'

export default {
  components: {
    SparqlQuery,
    EchartsView
  },
  data() {
    return {
      queryString: '',
    }
  },
  methods: {
    getQueryString(data) {
      this.queryString = data;
    }
  }
}
</script>

<style>
.container {
  min-height: 100vh;
  margin: 30px 30px;
  padding: 0;
}
  .left-container {
    float: left;
  }
  .right-container {
    float: right;
  }
</style>

