<template>
			<chart :options="chartOptions" auto-resize>
			</chart>
</template>

<script>
import ECharts from 'vue-echarts/components/ECharts.vue'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/title'

import 'echarts/lib/chart/line'

export default {
  components: {
    chart: ECharts
  },
  data () {
    return {
      chartOptions: {
        title: {
          left: 'center',
          text: 'Shareholding'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          data: ['01357', '08182', '02280']
        },
        yAxis: {
          splitLine: {show: false}
        },
        series: [{
          type: 'line',
          data: [5, 20, 36]
        }]
      }
    }
  }
}
</script>

<style>
.echarts {
  width: 100vw !important;
  height: 100vh !important;
  margin-top: 64px;
}
</style>