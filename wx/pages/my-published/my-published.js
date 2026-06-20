// pages/my-published/my-published.js
Page({
  data: {
    publishList: []
  },

  onShow() {
    // 每次进页面刷新数据
    this.getMyPublish()
  },

  // 获取我的发布列表
  getMyPublish() {
    const user = getApp().globalData.userInfo
    // 未登录拦截
    if (!user || !user.userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    const userId = user.userId

    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/publish/my/list?userId=" + userId,
      method: "GET",
      success: res => {
        wx.hideLoading()
        if (res.data.code === 200) {
          this.setData({ publishList: res.data.data })
        } else {
          wx.showToast({ title: res.data.msg || '暂无数据', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '网络请求失败', icon: 'none' })
      }
    })
  },

  // 跳转详情
  goDetail(e) {
    const type = e.currentTarget.dataset.type
    const id = e.currentTarget.dataset.id
    let url = ''
    switch (type) {
      case 1: url = '/pages/second/detail?id=' + id; break   //闲置
      case 2: url = '/pages/purchase/detail?id=' + id; break //求购
      case 3: url = '/pages/news/detail?id=' + id; break     //动态
      case 4: url = '/pages/lost/detail?id=' + id; break     //失物
    }
    if (url) wx.navigateTo({ url })
  }
})