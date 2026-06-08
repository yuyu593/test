const { get } = require('../../utils/request')

Page({
  data: {
    collectList: []
  },

  onShow() {
    this.getCollectList()
  },

  async getCollectList() {
    const user = getApp().globalData.userInfo
    if (!user || !user.userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }

    try {
      // 接口直接返回数组，无需处理 code/data
      const collectList = await get(`/collect/list/${user.userId}`)
      console.log('收藏接口返回:', collectList)
      
      this.setData({
        collectList: collectList || []
      })

    } catch (err) {
      console.error('加载收藏失败：', err)
      this.setData({ collectList: [] })
    }
  },

  // 点击跳转到对应详情页
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