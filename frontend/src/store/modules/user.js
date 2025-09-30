const state = {
  token: localStorage.getItem('token') || '',
  userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    localStorage.setItem('token', token)
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  },
  CLEAR_USER(state) {
    state.token = ''
    state.userInfo = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
}

const actions = {
  // 登录
  async login({ commit }, userInfo) {
    try {
      // 动态导入API模块，避免循环依赖
      const { login: loginApi, getUserInfo } = await import('@/api/user')
      
      const res = await loginApi(userInfo)
      if (res.code === 200 && res.data) {
        const { token } = res.data
        commit('SET_TOKEN', token)
        
        // 获取用户信息
        const userRes = await getUserInfo()
        if (userRes.code === 200) {
          commit('SET_USER_INFO', userRes.data)
        }
        
        return res.data
      } else {
        throw new Error(res.message || '登录失败')
      }
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  },
  
  // 登出
  async logout({ commit }) {
    try {
      const { logout: logoutApi } = await import('@/api/user')
      await logoutApi()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      commit('CLEAR_USER')
    }
  },
  
  // 获取用户信息
  async getUserInfo({ commit }) {
    try {
      const { getUserInfo } = await import('@/api/user')
      const res = await getUserInfo()
      if (res.code === 200) {
        commit('SET_USER_INFO', res.data)
        return res.data
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
