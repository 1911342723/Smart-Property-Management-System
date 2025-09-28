<template>
  <div @click="click">
    <svg-icon :icon-class="isFullscreen ? 'exit-fullscreen' : 'fullscreen'" />
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import screenfull from 'screenfull'

export default {
  name: 'Screenfull',
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
