App({
  onLaunch() {
    const userInfo = wx.getStorageSync('userInfo')
    this.globalData = {
      baseUrl: 'http://localhost:8080/campus',
      userInfo: userInfo,
      categoryType: ''
    }

    if (userInfo) {
      wx.switchTab({ url: '/pages/index/index' })
    }

    // 启动时也检查未读消息数，更新 tabBar 角标
    this.checkUnread()
  },
  onShow() {
    // 每次切到前台都检查未读消息数，更新 tabBar 角标
    this.checkUnread()
  },
  checkUnread() {
    const userInfo = wx.getStorageSync('userInfo')
    const userId = wx.getStorageSync('userId') || (userInfo && (userInfo.userid || userInfo.userId))
    if (!userId) return
    wx.request({
      url: "http://127.0.0.1:8080/campus/message/unread/count/" + userId,
      success: res => {
        if (res.data.code === 200) {
          const count = res.data.data || 0
          if (count > 0) {
            wx.setTabBarBadge({
              index: 3,
              text: count > 99 ? '99+' : String(count)
            })
          } else {
            wx.removeTabBarBadge({ index: 3 })
          }
        }
      }
    })
  },
  globalData: {
    userInfo: null,
    baseUrl: '',
    categoryType: ''
  }
})