<template>
    <div class="query">
      <p>请输入SPARQL查询语句：</p>
      <textarea class="queryInput" v-model="queryString" :placeholder="queryString" @on-keydown="textareaTab"></textarea>
      <button class="queryButton" @click="startQuery">查询</button>
    </div>
</template>

<script>
    export default {
      name: "SparqlQuery",
      data() {
          return {
            queryString: 'select ?s ?p ?o where {' +
            '?s ?p ?o.' +
            '}',
          }
      },
      methods: {
        textareaTab(e) {
          if (e.keyCode === 9) {
            if (!this.queryString) {
              this.queryString = '';
            }
            this.queryString += '\t';
            // 阻止默认切换元素的行为
            if (e && e.preventDefault()) {
              e.preventDefault();
            } else {
              window.event.returnValue = false;
            }
          }
        },
        startQuery(e) {
          this.$emit("listenQueryButton", this.queryString);
        }
      },
    }
</script>

<style scoped>
  .query {
    background-color: #357274;
    padding: 5px;
    position: relative;
  }
  .queryInput {
    display: inline-block;
    min-height: 250px;
    min-width: 400px;
    background-color: white;
  }
  .queryButton {
    font-size: 15px;
    display: block;
    width: 60px;
    height: 30px;
    border-radius: 22%;
    border: none;
    background-color: cadetblue;
    position: relative;
    left: 50%;
    margin-left: -30px;
    cursor: pointer;
    outline: none;
  }
</style>
