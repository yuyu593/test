const { get } = require('../../utils/request')
Page({
  data: { list: [] },

  onLoad() { this.getList() },

  async getList() {
    const data = await get('/second/list')
    this.setData({ list: data })
  },

  goPublish() {
    wx.navigateTo({ url: '/pages/second/publish' })
  }
})