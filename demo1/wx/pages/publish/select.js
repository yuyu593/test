Page({
  data: {
    currentDate: "" // 存放当前时间
  },

  // 页面加载时自动获取当前时间
  onLoad() {
    let date = new Date();
    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let day = date.getDate();
    
    // 格式：2026年4月20日
    let time = `${year}年${month}月${day}日`;
    this.setData({
      currentDate: time
    });
  },

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
    wx.navigateTo({ url: '/pages/purchase/publish' })
  },

  // 跳转到分享校园动态
  goPublishDynamic() {
    wx.navigateTo({ url: '/pages/news/publish' })
  },

  // 跳转到发布失物招领
  goPublishLost() {
    wx.navigateTo({ url: '/pages/lost/publish'})
  }
})