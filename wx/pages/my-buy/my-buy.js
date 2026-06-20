const { get } = require('../../utils/request')

Page({
  data: {
    baseUrl: "http://127.0.0.1:8080/campus/file/",
    currentTab: "",
    orderList: []
  },

  onShow() {
    this.getOrderList()
  },

  switchTab(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ currentTab: type })
    this.getOrderList()
  },

  async getOrderList() {
    const user = getApp().globalData.userInfo
    if (!user || !user.userId) {
      return
    }
    wx.showLoading({ title: '加载中...' })
    try {
      const list = await get(`/purchaseRecord/user/${user.userId}`)
      const { currentTab } = this.data
      let filteredList = list || []
      if (currentTab !== '') {
        filteredList = filteredList.filter(item => item.status === parseInt(currentTab))
      }
      this.setData({ orderList: filteredList })
    } catch (e) {
      console.error("加载订单失败", e)
      this.setData({ orderList: [] })
    } finally {
      wx.hideLoading()
    }
  },

  goPay(e) {
    const recordId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/purchase/pay?recordId=${recordId}`
    })
  },

  getStatusText(status) {
    const map = {
      0: '待付款',
      1: '已付款',
      2: '已完成',
      3: '已取消'
    }
    return map[status] || '未知'
  },

  getStatusClass(status) {
    const map = {
      0: 'pending',
      1: 'paid',
      2: 'completed',
      3: 'cancelled'
    }
    return map[status] || ''
  },

  formatTime(timeStr) {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    const year = date.getFullYear()
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
    const hour = date.getHours().toString().padStart(2, '0')
    const minute = date.getMinutes().toString().padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}`
  }
})