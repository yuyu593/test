Page({
  data: {
    user: {}
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