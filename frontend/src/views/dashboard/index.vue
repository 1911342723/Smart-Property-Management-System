<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1 class="page-title">数据仪表盘</h1>
      <p class="page-subtitle">实时监控物业管理各项指标</p>
    </div>

    <!-- KPI 卡片 -->
    <div class="kpi-cards">
      <el-row :gutter="20">
        <el-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24">
          <div class="kpi-card occupancy">
            <div class="kpi-content">
              <div class="kpi-icon">
                <el-icon><House /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-value">{{ kpiData.occupancyRate }}%</div>
                <div class="kpi-label">入住率</div>
                <div class="kpi-trend" :class="kpiData.occupancyTrend > 0 ? 'up' : 'down'">
                  <el-icon><ArrowUp v-if="kpiData.occupancyTrend > 0" /><ArrowDown v-else /></el-icon>
                  {{ Math.abs(kpiData.occupancyTrend) }}%
                </div>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24">
          <div class="kpi-card repair">
            <div class="kpi-content">
              <div class="kpi-icon">
                <el-icon><Tools /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-value">{{ kpiData.repairCompletionRate }}%</div>
                <div class="kpi-label">报修完成率</div>
                <div class="kpi-trend" :class="kpiData.repairTrend > 0 ? 'up' : 'down'">
                  <el-icon><ArrowUp v-if="kpiData.repairTrend > 0" /><ArrowDown v-else /></el-icon>
                  {{ Math.abs(kpiData.repairTrend) }}%
                </div>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24">
          <div class="kpi-card payment">
            <div class="kpi-content">
              <div class="kpi-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-value">{{ kpiData.paymentRate }}%</div>
                <div class="kpi-label">当月收费率</div>
                <div class="kpi-trend" :class="kpiData.paymentTrend > 0 ? 'up' : 'down'">
                  <el-icon><ArrowUp v-if="kpiData.paymentTrend > 0" /><ArrowDown v-else /></el-icon>
                  {{ Math.abs(kpiData.paymentTrend) }}%
                </div>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24">
          <div class="kpi-card satisfaction">
            <div class="kpi-content">
              <div class="kpi-icon">
                <el-icon><StarFilled /></el-icon>
              </div>
              <div class="kpi-info">
                <div class="kpi-value">{{ kpiData.satisfactionScore }}</div>
                <div class="kpi-label">业主满意度</div>
                <div class="kpi-trend" :class="kpiData.satisfactionTrend > 0 ? 'up' : 'down'">
                  <el-icon><ArrowUp v-if="kpiData.satisfactionTrend > 0" /><ArrowDown v-else /></el-icon>
                  {{ Math.abs(kpiData.satisfactionTrend).toFixed(1) }}
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :xl="16" :lg="24">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>业主满意度趋势</span>
                <el-select v-model="satisfactionPeriod" size="small" style="width: 120px;">
                  <el-option label="近7天" value="7days" />
                  <el-option label="近30天" value="30days" />
                  <el-option label="近3个月" value="3months" />
                </el-select>
              </div>
            </template>
            <div class="chart-container">
              <v-chart class="chart" :option="satisfactionChartOption" />
            </div>
          </el-card>
        </el-col>
        
        <el-col :xl="8" :lg="24">
          <el-card class="chart-card">
            <template #header>
              <span>工单类型分布</span>
            </template>
            <div class="chart-container">
              <v-chart class="chart" :option="workOrderPieOption" />
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :xl="12" :lg="24">
          <el-card class="chart-card">
            <template #header>
              <span>月度收费统计</span>
            </template>
            <div class="chart-container">
              <v-chart class="chart" :option="paymentBarOption" />
            </div>
          </el-card>
        </el-col>
        
        <el-col :xl="12" :lg="24">
          <el-card class="chart-card">
            <template #header>
              <span>入住率变化</span>
            </template>
            <div class="chart-container">
              <v-chart class="chart" :option="occupancyLineOption" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <el-card>
        <template #header>
          <span>快速操作</span>
        </template>
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="$router.push('/work-order/list')">
            <el-icon><Document /></el-icon>
            查看工单
          </el-button>
          <el-button type="success" size="large" @click="$router.push('/finance/bill')">
            <el-icon><Receipt /></el-icon>
            生成账单
          </el-button>
          <el-button type="warning" size="large" @click="$router.push('/community/notice')">
            <el-icon><Bell /></el-icon>
            发布公告
          </el-button>
          <el-button type="info" size="large" @click="$router.push('/user/owner')">
            <el-icon><User /></el-icon>
            用户管理
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { House, Tools, Money, StarFilled, ArrowUp, ArrowDown, Document, Receipt, Bell, User } from '@element-plus/icons-vue'

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

export default {
  name: 'Dashboard',
  components: {
    VChart,
    House,
    Tools,
    Money,
    StarFilled,
    ArrowUp,
    ArrowDown,
    Document,
    Receipt,
    Bell,
    User
  },
  setup() {
    const satisfactionPeriod = ref('30days')
    
    const kpiData = reactive({
      occupancyRate: 92.5,
      occupancyTrend: 2.3,
      repairCompletionRate: 87.2,
      repairTrend: 5.1,
      paymentRate: 94.8,
      paymentTrend: -1.2,
      satisfactionScore: 4.6,
      satisfactionTrend: 0.3
    })
    
    // 业主满意度趋势图
    const satisfactionChartOption = ref({
      tooltip: {
        trigger: 'axis',
        backgroundColor: '#2f3136',
        borderColor: '#40444b',
        textStyle: { color: '#ffffff' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
        axisLine: { lineStyle: { color: '#40444b' } },
        axisLabel: { color: '#b9bbbe' }
      },
      yAxis: {
        type: 'value',
        min: 3,
        max: 5,
        axisLine: { lineStyle: { color: '#40444b' } },
        axisLabel: { color: '#b9bbbe' },
        splitLine: { lineStyle: { color: '#40444b' } }
      },
      series: [{
        name: '满意度',
        type: 'line',
        data: [4.2, 4.3, 4.1, 4.4, 4.5, 4.3, 4.6, 4.5, 4.7, 4.6, 4.8, 4.6],
        smooth: true,
        lineStyle: { color: '#5865f2', width: 3 },
        itemStyle: { color: '#5865f2' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(88, 101, 242, 0.3)' },
              { offset: 1, color: 'rgba(88, 101, 242, 0.05)' }
            ]
          }
        }
      }]
    })
    
    // 工单类型分布饼图
    const workOrderPieOption = ref({
      tooltip: {
        trigger: 'item',
        backgroundColor: '#2f3136',
        borderColor: '#40444b',
        textStyle: { color: '#ffffff' }
      },
      legend: {
        orient: 'vertical',
        right: '10%',
        top: 'center',
        textStyle: { color: '#b9bbbe' }
      },
      series: [{
        name: '工单类型',
        type: 'pie',
        radius: '70%',
        center: ['40%', '50%'],
        data: [
          { value: 35, name: '维修类', itemStyle: { color: '#5865f2' } },
          { value: 25, name: '投诉类', itemStyle: { color: '#3ba55c' } },
          { value: 20, name: '咨询类', itemStyle: { color: '#faa61a' } },
          { value: 15, name: '建议类', itemStyle: { color: '#ed4245' } },
          { value: 5, name: '其他', itemStyle: { color: '#747f8d' } }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    })
    
    // 月度收费统计柱状图
    const paymentBarOption = ref({
      tooltip: {
        trigger: 'axis',
        backgroundColor: '#2f3136',
        borderColor: '#40444b',
        textStyle: { color: '#ffffff' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月'],
        axisLine: { lineStyle: { color: '#40444b' } },
        axisLabel: { color: '#b9bbbe' }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#40444b' } },
        axisLabel: { color: '#b9bbbe' },
        splitLine: { lineStyle: { color: '#40444b' } }
      },
      series: [{
        name: '收费金额',
        type: 'bar',
        data: [120000, 135000, 142000, 138000, 145000, 152000],
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: '#5865f2' },
              { offset: 1, color: '#4752c4' }
            ]
          }
        },
        emphasis: {
          itemStyle: { color: '#3c45a5' }
        }
      }]
    })
    
    // 入住率变化折线图
    const occupancyLineOption = ref({
      tooltip: {
        trigger: 'axis',
        backgroundColor: '#2f3136',
        borderColor: '#40444b',
        textStyle: { color: '#ffffff' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月'],
        axisLine: { lineStyle: { color: '#40444b' } },
        axisLabel: { color: '#b9bbbe' }
      },
      yAxis: {
        type: 'value',
        min: 80,
        max: 100,
        axisLine: { lineStyle: { color: '#40444b' } },
        axisLabel: { color: '#b9bbbe' },
        splitLine: { lineStyle: { color: '#40444b' } }
      },
      series: [{
        name: '入住率',
        type: 'line',
        data: [88.5, 89.2, 90.1, 91.3, 92.0, 92.5],
        smooth: true,
        lineStyle: { color: '#3ba55c', width: 3 },
        itemStyle: { color: '#3ba55c' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(59, 165, 92, 0.3)' },
              { offset: 1, color: 'rgba(59, 165, 92, 0.05)' }
            ]
          }
        }
      }]
    })
    
    onMounted(() => {
      // 模拟数据加载
      console.log('Dashboard loaded')
    })
    
    return {
      satisfactionPeriod,
      kpiData,
      satisfactionChartOption,
      workOrderPieOption,
      paymentBarOption,
      occupancyLineOption
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.dashboard-container {
  padding: 20px;
}

.dashboard-header {
  margin-bottom: 32px;
  
  .page-title {
    font-size: 32px;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 8px 0;
  }
  
  .page-subtitle {
    font-size: 16px;
    color: $text-secondary;
    margin: 0;
  }
}

.kpi-cards {
  margin-bottom: 32px;
  
  .kpi-card {
    background: linear-gradient(135deg, $bg-tertiary 0%, lighten($bg-tertiary, 2%) 100%);
    border-radius: 16px;
    padding: 24px;
    border: 1px solid $border-color;
    transition: all 0.3s ease;
    height: 140px;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    }
    
    &.occupancy {
      border-left: 4px solid #3ba55c;
    }
    
    &.repair {
      border-left: 4px solid #5865f2;
    }
    
    &.payment {
      border-left: 4px solid #faa61a;
    }
    
    &.satisfaction {
      border-left: 4px solid #ed4245;
    }
    
    .kpi-content {
      display: flex;
      align-items: center;
      gap: 20px;
      height: 100%;
    }
    
    .kpi-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: $text-primary;
      background: rgba(88, 101, 242, 0.1);
      
      .occupancy & {
        background: rgba(59, 165, 92, 0.1);
        color: #3ba55c;
      }
      
      .repair & {
        background: rgba(88, 101, 242, 0.1);
        color: #5865f2;
      }
      
      .payment & {
        background: rgba(250, 166, 26, 0.1);
        color: #faa61a;
      }
      
      .satisfaction & {
        background: rgba(237, 66, 69, 0.1);
        color: #ed4245;
      }
    }
    
    .kpi-info {
      flex: 1;
    }
    
    .kpi-value {
      font-size: 36px;
      font-weight: 700;
      color: $text-primary;
      line-height: 1;
      margin-bottom: 8px;
    }
    
    .kpi-label {
      font-size: 16px;
      color: $text-secondary;
      margin-bottom: 6px;
    }
    
    .kpi-trend {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 14px;
      font-weight: 500;
      
      &.up {
        color: #3ba55c;
      }
      
      &.down {
        color: #ed4245;
      }
    }
  }
}

.charts-section {
  margin-bottom: 32px;
  
  .chart-card {
    background-color: $bg-tertiary !important;
    border: 1px solid $border-color !important;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;
      color: $text-primary;
    }
    
    .chart-container {
      height: 400px;
      
      .chart {
        width: 100%;
        height: 100%;
      }
    }
  }
}

.quick-actions {
  .action-buttons {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
    
    .el-button {
      flex: 1;
      min-width: 140px;
      height: 60px;
      font-size: 16px;
      font-weight: 500;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      
      .el-icon {
        font-size: 20px;
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .kpi-cards .kpi-card {
    margin-bottom: 16px;
  }
  
  .charts-section .chart-container {
    height: 300px;
  }
  
  .quick-actions .action-buttons {
    flex-direction: column;
    
    .el-button {
      min-width: auto;
    }
  }
}
</style>
