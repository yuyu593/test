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
    // 新增：请求发布、卖出数量
    if(user.userId){
      this.getCountData(user.userId)
    }
  },

  // 新增方法：获取发布数+卖出数
  getCountData(userId){
    // 获取发布总数
    wx.request({
      url:"http://127.0.0.1:8080/campus/publish/count?userId="+userId,
      success:res=>{
        if(res.data.code===200){
          this.setData({"user.publishCount":res.data.data})
        }
      }
    })
    // 获取卖出总数
    wx.request({
      url:"http://127.0.0.1:8080/campus/second/sell/count?userId="+userId,
      success:res=>{
        if(res.data.code===200){
          this.setData({"user.soldCount":res.data.data})
        }
      }
    })
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
  // 跳转到我卖出的页面
  goSell(){
    wx.navigateTo({
      url:"/pages/my-sell/my-sell"
    })
  },
  // 跳转到我买到的页面
  goBuy(){
    wx.navigateTo({
      url:"/pages/my-buy/my-buy"
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