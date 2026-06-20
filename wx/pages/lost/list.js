Page({
  data: {
    baseUrl: "http://127.0.0.1:8080/campus/file/",
    currentTab: "",
    list: []
  },

  onShow() {
    this.loadList()
  },

  switchTab(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ currentTab: type }, () => {
      this.loadList()
    })
  },

  loadList() {
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/lost/list",
      data: { type: this.data.currentTab || null },
      success: res => {
        if (res.data.code === 200) {
          const list = (res.data.data || []).map(item => {
            if (item.imgUrls) {
              const firstImg = item.imgUrls.split(',')[0].trim()
              item.imgUrl = this.data.baseUrl + firstImg
            } else {
              item.imgUrl = ''
            }
            const t = item.createTime || ''
            item.timeStr = t.substring(0, 10).replace(/-/g, '/')
            return item
          })
          this.setData({ list })
        }
      },
      complete: () => wx.hideLoading()
    })
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/lost/detail?id=' + id })
  },

  goPublish() {
    wx.navigateTo({ url: '/pages/lost/publish' })
  }
})
