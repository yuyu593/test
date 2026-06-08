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
  },
  //跳转到我发布的页面
  gomypublish() {
    wx.navigateTo({
      url: '/pages/my-published/my-published'
    })
  },
  // 跳转到我的收藏页面
  goCollect() {
    wx.navigateTo({
      url: '/pages/collect/collect'
    })
  },
  //跳转到浏览记录页面
  goHistory() {
    wx.navigateTo({
      url: "/pages/history/history"
    })
  },
  //跳转到反馈页面
  goFeedback() {
    wx.navigateTo({
      url: '/pages/feedback/feedback'
    });
  },
  //跳转到关于平台页面
  goToAbout() {
    wx.navigateTo({
      url: '/pages/about/about'
    });
  }
})