Page({
  // 关闭页面
  close() {
    wx.navigateBack()
  },

  // 跳转到发布闲置物品
  goPublishGoods() {
    wx.navigateTo({ url: '/pages/second/publish' })
  },

  // 跳转到发布求购信息
  goPublishBuy() {
    wx.showToast({ title: '功能开发中', icon: 'none' })
  },

  // 跳转到分享校园动态
  goPublishDynamic() {
    wx.showToast({ title: '功能开发中', icon: 'none' })
  },

  // 跳转到发布失物招领
  goPublishLost() {
    wx.navigateTo({ url: '/pages/lost/publish' })
  }
})