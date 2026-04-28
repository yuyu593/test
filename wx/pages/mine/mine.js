Page({
  data: {
    user: {},
    // 关键：和你列表页保持一致的图片前缀
    baseUrl: "http://127.0.0.1:8080/campus/file/"
  },

  onShow() {
    // 从全局获取用户信息
    const user = getApp().globalData.userInfo || {}
    this.setData({ user })
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: res => {
        if (res.confirm) {
          wx.clearStorageSync()
          getApp().globalData.userInfo = null
          wx.redirectTo({ url: '/pages/login/login' })
        }
      }
    })
  }
})