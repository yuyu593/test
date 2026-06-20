Page({
  data: {
    historyList: []
  },

  onShow() {
    this.getLocalHistory()
  },

  // 读取本地缓存的浏览记录
  getLocalHistory() {
    try {
      const list = wx.getStorageSync('viewHistory') || []
      this.setData({
        historyList: list
      })
    } catch (err) {
      console.error("读取浏览记录失败", err)
      this.setData({ historyList: [] })
    }
  },
  // 清空本地浏览记录
  clearHistory() {
    wx.showModal({
      title: "提示",
      content: "确定清空所有浏览记录吗？",
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('viewHistory')
          this.setData({ historyList: [] })
          wx.showToast({ title: "已清空", icon: "none" })
        }
      }
    })
  },
  // 点击跳转到对应详情
  goDetail(e) {
    const type = parseInt(e.currentTarget.dataset.type)
    const id = e.currentTarget.dataset.id
    let url = ''

    switch (type) {
      case 1: // 闲置
        url = `/pages/second/detail?id=${id}`
        break
      case 2: // 求购
        url = `/pages/purchase/detail?id=${id}`
        break
      case 3: // 动态
        url = `/pages/news/detail?id=${id}`
        break
      case 4: // 失物
        url = `/pages/lost/detail?id=${id}`
        break
      default:
        return
    }
    wx.navigateTo({ url })
  }
})