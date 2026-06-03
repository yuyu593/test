App({
  onLaunch() {
    const userInfo = wx.getStorageSync('userInfo')
    this.globalData = {
      baseUrl: 'http://localhost:8080/campus', // 这里必须加 /campus
      userInfo: userInfo
    }

    if (userInfo) {
      wx.switchTab({ url: '/pages/index/index' })
    }
  },
  globalData: {
    userInfo: null,
    baseUrl: ''
  }
})