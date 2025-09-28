<template>
  <div @click="click">
    <el-icon>
      <FullScreen v-if="!isFullscreen" />
      <Aim v-else />
    </el-icon>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import screenfull from 'screenfull'
import { FullScreen, Aim } from '@element-plus/icons-vue'

export default {
  name: 'Screenfull',
  components: {
    FullScreen,
    Aim
  },
  setup() {
    const isFullscreen = ref(false)
    
    const click = () => {
      if (!screenfull.isEnabled) {
        console.warn('you browser can not work')
        return false
      }
      screenfull.toggle()
    }
    
    const change = () => {
      isFullscreen.value = screenfull.isFullscreen
    }
    
    const init = () => {
      if (screenfull.isEnabled) {
        screenfull.on('change', change)
      }
    }
    
    const destroy = () => {
      if (screenfull.isEnabled) {
        screenfull.off('change', change)
      }
    }
    
    onMounted(() => {
      init()
    })
    
    onUnmounted(() => {
      destroy()
    })
    
    return {
      isFullscreen,
      click
    }
  }
}
</script>

<style scoped>
.screenfull-svg {
  display: inline-block;
  cursor: pointer;
  fill: #5a5e66;
  width: 20px;
  height: 20px;
  vertical-align: 10px;
}
</style>
