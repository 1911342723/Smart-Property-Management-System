<template>
  <div :class="{'show': show}" class="header-search">
    <el-icon class="search-icon" @click.stop="click">
      <Search />
    </el-icon>
    <el-select
      ref="headerSearchSelect"
      v-model="search"
      :remote-method="querySearch"
      filterable
      default-first-option
      remote
      placeholder="搜索"
      class="header-search-select"
      @change="change"
    >
      <el-option
        v-for="option in options"
        :key="option.item.path"
        :value="option.item"
        :label="option.item.title.join(' > ')"
      />
    </el-select>
  </div>
</template>

<script>
import { ref, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'HeaderSearch',
  components: {
    Search
  },
  setup() {
    const router = useRouter()
    const search = ref('')
    const options = ref([])
    const searchPool = ref([])
    const show = ref(false)
    const fuse = ref(null)
    const headerSearchSelect = ref(null)
    
    const routes = computed(() => router.getRoutes())
    
    const click = () => {
      show.value = !show.value
      if (show.value) {
        headerSearchSelect.value && headerSearchSelect.value.focus()
      }
    }
    
    const close = () => {
      headerSearchSelect.value && headerSearchSelect.value.blur()
      options.value = []
      show.value = false
    }
    
    const change = (val) => {
      router.push(val.path)
      search.value = ''
      options.value = []
      nextTick(() => {
        show.value = false
      })
    }
    
    const initFuse = (list) => {
      // 简化版搜索，不使用fuse.js
      searchPool.value = list
    }
    
    const generateRoutes = (routes, basePath = '/', prefixTitle = []) => {
      let res = []
      
      for (const router of routes) {
        if (router.hidden) { continue }
        
        const data = {
          path: basePath + router.path,
          title: [...prefixTitle]
        }
        
        if (router.meta && router.meta.title) {
          data.title = [...data.title, router.meta.title]
          
          if (router.redirect !== 'noRedirect') {
            res.push(data)
          }
        }
        
        if (router.children) {
          const tempRoutes = generateRoutes(router.children, data.path + '/', data.title)
          if (tempRoutes.length >= 1) {
            res = [...res, ...tempRoutes]
          }
        }
      }
      return res
    }
    
    const querySearch = (query) => {
      if (query !== '') {
        options.value = searchPool.value.filter(item => {
          return item.title.join('').toLowerCase().indexOf(query.toLowerCase()) > -1
        }).map(item => ({ item }))
      } else {
        options.value = []
      }
    }
    
    watch(routes, () => {
      const list = generateRoutes(routes.value)
      initFuse(list)
    }, { immediate: true })
    
    return {
      search,
      options,
      show,
      headerSearchSelect,
      click,
      close,
      change,
      querySearch
    }
  }
}
</script>

<style lang="scss" scoped>
.header-search {
  font-size: 0 !important;

  .search-icon {
    cursor: pointer;
    font-size: 18px;
    vertical-align: middle;
  }

  .header-search-select {
    font-size: 18px;
    transition: width 0.2s;
    width: 0;
    overflow: hidden;
    background: transparent;
    border-radius: 0;
    display: inline-block;
    vertical-align: middle;

    ::v-deep(.el-input__inner) {
      border-radius: 0;
      border: 0;
      padding-left: 0;
      padding-right: 0;
      box-shadow: none !important;
      border-bottom: 1px solid #d9d9d9;
      vertical-align: middle;
    }
  }

  &.show {
    .header-search-select {
      width: 210px;
      margin-left: 10px;
    }
  }
}
</style>
