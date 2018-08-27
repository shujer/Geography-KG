<template>
    <div id="myChart" class="graph">
    </div>
</template>

<script>
  let echarts = require("echarts");
    export default {
      name: "EchartsView",
      data() {
          return {

          }
      },
      mounted() {
        let myChart = echarts.init(document.getElementById("myChart"));
        let option = {
          title: {
            show: false,
          },
          tooltip: {},
          animationDurationUpdate: 1500,
          animationEasingUpdate: 'quinticInOut',
          series : [
            {
              type: 'graph',
              layout: 'force',
              force: {
                repulsion: 3000,
              },
              name: 'queryResult',
              symbolSize: 35,
              roam: true,
              draggable: true,
              focusNodeAdjacency: true,
              edgeSymbol: ['circle', 'arrow'],
              edgeSymbolSize: [4, 10],
              label: {
                normal: {
                  show: true
                }
              },
              edgeLabel: {
                normal: {
                  textStyle: {
                    fontSize: 12
                  }
                }
              },
              data: [{
                name: '节点1'
              }, {
                name: '节点2'
              }, {
                name: '节点3'
              }, {
                name: '节点4'
              }],
              // links: [],
              links: [{
                source: '节点1',
                target: '节点2',
              }, {
                source: '节点2',
                target: '节点1',
              }, {
                source: '节点1',
                target: '节点3'
              }, {
                source: '节点2',
                target: '节点3'
              }, {
                source: '节点2',
                target: '节点4'
              }, {
                source: '节点1',
                target: '节点4'
              }],
              lineStyle: {
                normal: {
                  opacity: 0.9,
                  width: 2,
                  curveness: 0
                }
              }
            }
          ]
        };
        //myChart.showLoading();
        myChart.setOption(option);
      },
    }
</script>

<style scoped>
  .graph {
    min-width: 600px;
    min-height: 550px;
    border: 2px solid #357274;
    border-radius: 5px;
    padding: 5px;
  }
</style>
