// custom-tab-bar/index.js
const app = getApp()

Component({
  data: {
    selected: 0,
    color: "#8e9297",
    selectedColor: "#2d3748",
    list: [],
    // 不同角色的tabbar配置
    roleTabs: {
      owner: [
        {
          pagePath: "/pages/owner/home",
          iconUrl: "/static/icons/home.svg",
          text: "首页"
        },
        {
          pagePath: "/pages/owner/repair",
          iconUrl: "/static/icons/repair.svg",
          text: "报修"
        },
        {
          pagePath: "/pages/owner/messages",
          iconUrl: "/static/icons/message.svg",
          text: "消息"
        },
        {
          pagePath: "/pages/owner/profile",
          iconUrl: "/static/icons/profile.svg",
          text: "我的"
        }
      ],
      worker: [
        {
          pagePath: "/pages/worker/dashboard",
          iconUrl: "/static/icons/home.svg",
          text: "工作台"
        },
        {
          pagePath: "/pages/worker/orders",
          iconUrl: "/static/icons/repair.svg",
          text: "工单"
        },
        {
          pagePath: "/pages/worker/profile",
          iconUrl: "/static/icons/profile.svg",
          text: "我的"
        }
      ],
      guard: [
        {
          pagePath: "/pages/guard/tasks",
          iconUrl: "/static/icons/home.svg",
          text: "任务"
        },
        {
          pagePath: "/pages/guard/gate",
          iconUrl: "/static/icons/door.svg",
          text: "门岗"
        },
        {
          pagePath: "/pages/guard/profile",
          iconUrl: "/static/icons/profile.svg",
          text: "我的"
        }
      ]
    }
  },

  attached() {
    // 组件初始化时设置当前选中的tab
    this.initTabBar()
  },

  methods: {
    // 初始化tabbar
    initTabBar() {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      
      // 后端返回的是 userType 字段，值为大写（OWNER, WORKER, GUARD）
      let userRole = 'owner' // 默认为业主
      
      if (userInfo?.userType) {
        userRole = userInfo.userType.toLowerCase() // 转换为小写
      }
      
      console.log('初始化TabBar - 用户角色:', userRole, '用户信息:', userInfo)
      
      const roleTabList = this.data.roleTabs[userRole] || this.data.roleTabs.owner
      
      this.setData({
        list: roleTabList
      }, () => {
        this.setSelected()
      })
    },

    // 切换tab
    switchTab(e) {
      const data = e.currentTarget.dataset
      const url = data.path
      
      wx.switchTab({
        url,
        success: () => {
          this.setData({
            selected: data.index
          })
        }
      })
    },

    // 设置选中状态
    setSelected() {
      const pages = getCurrentPages()
      if (pages.length === 0) return
      
      const currentPage = pages[pages.length - 1]
      const currentPath = `/${currentPage.route}`
      
      const selected = this.data.list.findIndex(item => item.pagePath === currentPath)
      if (selected !== -1) {
        this.setData({
          selected
        })
      }
    },

    // 更新选中状态（供页面调用）
    updateSelected(index) {
      this.setData({
        selected: index
      })
    },

    // 更新角色tabbar（供外部调用）
    updateRoleTabBar(userType) {
      // userType 可能是大写的 OWNER, WORKER, GUARD，需要转换为小写
      const role = userType ? userType.toLowerCase() : 'owner'
      const roleTabList = this.data.roleTabs[role] || this.data.roleTabs.owner
      
      console.log('更新角色TabBar:', userType, '->', role)
      
      this.setData({
        list: roleTabList,
        selected: 0
      })
    }
  }
})
