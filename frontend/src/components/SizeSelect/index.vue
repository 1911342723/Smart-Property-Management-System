<template>
  <el-dropdown trigger="click" @command="handleSetSize">
    <div>
      <el-icon>
        <Setting />
      </el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item of sizeOptions"
          :key="item.value"
          :disabled="size === item.value"
          :command="item.value"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { Setting } from '@element-plus/icons-vue'

export default {
  name: 'SizeSelect',
  components: {
    Setting
  },
  setup() {
    const store = useStore()
    
    const size = computed(() => store.state.app.size)
    
    const sizeOptions = [
      { label: 'Default', value: 'default' },
      { label: 'Large', value: 'large' },
      { label: 'Small', value: 'small' }
    ]
    
    const handleSetSize = (size) => {
      store.dispatch('app/setSize', size)
      location.reload()
    }
    
    return {
      size,
      sizeOptions,
      handleSetSize
    }
  }
}
</script>
