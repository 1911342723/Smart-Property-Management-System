<template>
  <div class="dashboard-container responsive-container">
    <!-- 页面标题 -->
    <div class="dashboard-header">
      <h1 class="page-title title-responsive">数据仪表盘</h1>
      <p class="page-subtitle text-responsive">实时监控物业管理各项指标</p>
    </div>

    <!-- KPI 卡片 -->
    <div class="kpi-cards responsive-grid grid-4">
      <div class="kpi-card occupancy responsive-card">
        <div class="kpi-content">
          <div class="kpi-icon">
            <el-icon><House /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.occupancyRate }}%</div>
            <div class="kpi-label">入住率</div>
            <div class="kpi-trend" :class="kpiData.occupancyTrend > 0 ? 'trend-up' : 'trend-down'">
              <el-icon><ArrowUp v-if="kpiData.occupancyTrend > 0" /><ArrowDown v-else /></el-icon>
              {{ Math.abs(kpiData.occupancyTrend) }}%
            </div>
          </div>
        </div>
      </div>
      
      <div class="kpi-card repair responsive-card">
        <div class="kpi-content">
          <div class="kpi-icon">
            <el-icon><Tools /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.repairCompletionRate }}%</div>
            <div class="kpi-label">报修完成率</div>
            <div class="kpi-trend" :class="kpiData.repairTrend > 0 ? 'trend-up' : 'trend-down'">
              <el-icon><ArrowUp v-if="kpiData.repairTrend > 0" /><ArrowDown v-else /></el-icon>
              {{ Math.abs(kpiData.repairTrend) }}%
            </div>
          </div>
        </div>
      </div>
      
      <div class="kpi-card payment responsive-card">
        <div class="kpi-content">
          <div class="kpi-icon">
            <el-icon><Money /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.paymentRate }}%</div>
            <div class="kpi-label">当月收费率</div>
            <div class="kpi-trend" :class="kpiData.paymentTrend > 0 ? 'trend-up' : 'trend-down'">
              <el-icon><ArrowUp v-if="kpiData.paymentTrend > 0" /><ArrowDown v-else /></el-icon>
              {{ Math.abs(kpiData.paymentTrend) }}%
            </div>
          </div>
        </div>
      </div>
      
      <div class="kpi-card satisfaction responsive-card">
        <div class="kpi-content">
          <div class="kpi-icon">
            <el-icon><StarFilled /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ kpiData.satisfactionScore }}</div>
            <div class="kpi-label">业主满意度</div>
            <div class="kpi-trend" :class="kpiData.satisfactionTrend > 0 ? 'trend-up' : 'trend-down'">
              <el-icon><ArrowUp v-if="kpiData.satisfactionTrend > 0" /><ArrowDown v-else /></el-icon>
              {{ Math.abs(kpiData.satisfactionTrend) }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="charts-grid">
      <!-- 业主满意度趋势图 -->
      <div class="chart-card responsive-card">
        <div class="card-header">
          <span>业主满意度趋势</span>
          <el-select v-model="satisfactionPeriod" size="small" style="width: 120px">
            <el-option label="最近7天" value="7days" />
            <el-option label="最近30天" value="30days" />
            <el-option label="最近90天" value="90days" />
          </el-select>
        </div>
        <div class="chart-container">
          <v-chart :option="satisfactionChartOption" />
        </div>
      </div>

      <!-- 工单分布饼图 -->
      <div class="chart-card responsive-card">
        <div class="card-header">
          <span>工单类型分布</span>
        </div>
        <div class="chart-container">
          <v-chart :option="workOrderPieOption" />
        </div>
      </div>

      <!-- 收费统计柱状图 -->
      <div class="chart-card responsive-card">
        <div class="card-header">
          <span>月度收费统计</span>
        </div>
        <div class="chart-container">
          <v-chart :option="paymentBarOption" />
        </div>
      </div>

      <!-- 入住率变化图 -->
      <div class="chart-card responsive-card">
        <div class="card-header">
          <span>入住率变化趋势</span>
        </div>
        <div class="chart-container">
          <v-chart :option="occupancyLineOption" />
        </div>
      </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions responsive-card">
      <h3 class="section-title">快捷操作</h3>
      <div class="action-buttons responsive-button-group">
        <el-button type="primary" size="large" @click="$router.push('/service/workorder')">
          <el-icon><Document /></el-icon>
          查看工单
        </el-button>
        <el-button type="success" size="large" @click="$router.push('/finance/bill')">
          <el-icon><CreditCard /></el-icon>
          生成账单
        </el-button>
        <el-button type="warning" size="large" @click="$router.push('/community/notice')">
          <el-icon><Bell /></el-icon>
          发布公告
        </el-button>
        <el-button type="info" size="large" @click="$router.push('/user/owner')">
          <el-icon><User /></el-icon>
          业主管理
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
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
import { House, Tools, Money, StarFilled, ArrowUp, ArrowDown, Document, CreditCard, Bell, User } from '@element-plus/icons-vue'

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
    CreditCard,
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

    // 响应式图表基础配置
    const getResponsiveChartConfig = () => {
      const isMobile = window.innerWidth < 768
      const isTablet = window.innerWidth < 1024
      
      return {
        grid: {
          left: isMobile ? '10%' : '3%',
          right: isMobile ? '10%' : '4%',
          bottom: isMobile ? '15%' : '3%',
          top: isMobile ? '15%' : '10%',
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          confine: true,
          textStyle: {
            fontSize: isMobile ? 10 : 12
          }
        },
        legend: {
          textStyle: {
            fontSize: isMobile ? 10 : 12
          }
        }
      }
    }

    // 图表配置
    const satisfactionChartOption = reactive({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月']
      },
      yAxis: { type: 'value', min: 0, max: 5 },
      series: [{
        data: [4.2, 4.3, 4.1, 4.5, 4.4, 4.6],
        type: 'line',
        smooth: true,
        itemStyle: { color: '#5865f2' }
      }]
    })

    const workOrderPieOption = reactive({
      tooltip: { trigger: 'item' },
      legend: { bottom: '5%', left: 'center' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        data: [
          { value: 35, name: '设备维修' },
          { value: 25, name: '清洁保养' },
          { value: 20, name: '安全检查' },
          { value: 15, name: '投诉处理' },
          { value: 5, name: '其他' }
        ]
      }]
    })

    const paymentBarOption = reactive({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月']
      },
      yAxis: { type: 'value' },
      series: [{
        data: [85000, 92000, 88000, 95000, 91000, 97000],
        type: 'bar',
        itemStyle: { color: '#38a169' }
      }]
    })

    const occupancyLineOption = reactive({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月']
      },
      yAxis: { type: 'value', min: 80, max: 100 },
      series: [{
        data: [88.5, 89.2, 90.1, 91.3, 91.8, 92.5],
        type: 'line',
        smooth: true,
        itemStyle: { color: '#d69e2e' }
      }]
    })

    onMounted(() => {
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
@import '@/styles/responsive.scss';

.dashboard-container {
  padding: 20px;
  min-height: calc(100vh - 50px);
  background: $bg-primary;
}

.dashboard-header {
  margin-bottom: 30px;
  text-align: center;
  
  .page-title {
    font-size: 32px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 8px;
    background: linear-gradient(135deg, #5865f2, #3c45a5);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  
  .page-subtitle {
    font-size: 16px;
    color: $text-secondary;
    opacity: 0.8;
  }
}

.kpi-cards {
  margin-bottom: 30px;
  
  .kpi-card {
    background: linear-gradient(135deg, $bg-tertiary 0%, lighten($bg-tertiary, 2%) 100%);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    padding: 24px;
    height: 140px;
    position: relative;
    overflow: hidden;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    }
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #5865f2, #3c45a5);
    }
    
    .kpi-content {
      display: flex;
      align-items: center;
      height: 100%;
      gap: 20px;
    }
    
    .kpi-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: white;
      
      .el-icon {
        font-size: 24px;
      }
    }
    
    .kpi-info {
      flex: 1;
      
      .kpi-value {
        font-size: 32px;
        font-weight: 700;
        color: $text-primary;
        line-height: 1;
        margin-bottom: 8px;
      }
      
      .kpi-label {
        font-size: 16px;
        color: $text-secondary;
        margin-bottom: 8px;
      }
      
      .kpi-trend {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 14px;
        font-weight: 600;
        
        &.trend-up {
          color: $success-color;
        }
        
        &.trend-down {
          color: $error-color;
        }
        
        .el-icon {
          font-size: 14px;
        }
      }
    }
    
    &.occupancy .kpi-icon {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    &.repair .kpi-icon {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    
    &.payment .kpi-icon {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }
    
    &.satisfaction .kpi-icon {
      background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    }
  }
}

.charts-section {
  margin-bottom: 30px;
  
  .charts-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;
    
    @media (max-width: 1200px) {
      grid-template-columns: 1fr 1fr;
    }
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 16px;
    }
    
    .chart-card:first-child {
      grid-row: 1 / 3;
      
      @media (max-width: 1200px) {
        grid-row: auto;
        grid-column: 1 / -1;
      }
    }
  }
  
  .chart-card {
    background: $bg-tertiary;
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    overflow: hidden;
    min-height: 200px;
    
    .card-header {
      padding: 20px 24px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 18px;
      font-weight: 600;
      color: $text-primary;
      background: linear-gradient(135deg, $bg-secondary 0%, lighten($bg-secondary, 2%) 100%);
      
      @media (max-width: 768px) {
        padding: 16px;
        font-size: 16px;
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
        
        .el-select {
          width: 100% !important;
          max-width: 120px;
        }
      }
    }
    
    .chart-container {
      height: 400px;
      padding: 20px;
      overflow: hidden;
      position: relative;
      
      @media (max-width: 768px) {
        height: 280px;
        padding: 16px;
      }
      
      .echarts {
        width: 100% !important;
        height: 100% !important;
        max-width: 100%;
        max-height: 100%;
      }
    }
  }
}

.quick-actions {
  background: $bg-tertiary;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 24px;
  
  .section-title {
    font-size: 20px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 8px;
    
    &::before {
      content: '';
      width: 4px;
      height: 20px;
      background: linear-gradient(135deg, #5865f2, #3c45a5);
      border-radius: 2px;
    }
  }
  
    .action-buttons {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 12px;
    }
    
    .el-button {
      width: 100%;
      height: 48px;
      border-radius: 8px;
      font-weight: 600;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
      
      .el-icon {
        margin-right: 8px;
        font-size: 16px;
      }
    }
  }
}
</style>
