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
  login({ commit }, userInfo) {
    return new Promise((resolve) => {
      // 模拟登录
      const token = 'mock-token-' + Date.now()
      const user = {
        id: 1,
        username: userInfo.username,
        name: '系统管理员',
        role: 'admin',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      }
      
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', user)
      resolve({ token, user })
    })
  },
  
  // 登出
  logout({ commit }) {
    return new Promise((resolve) => {
      commit('CLEAR_USER')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
