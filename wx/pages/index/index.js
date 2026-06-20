const { get } = require('../../utils/request')

Page({
  data: {
    baseUrl: "http://127.0.0.1:8080/campus/file/",
    categories: [
      { icon: '📢', name: '校园动态', color: 'blue', type: 'news' },
      { icon: '📦', name: '失物招领', color: 'green', type: 'lost' },
      { icon: '🛒', name: '求购中', color: 'orange', type: 'purchase' },
      { icon: '💰', name: '闲置物品', color: 'purple', type: 'second' }
    ],
    recommendGoods: [],
    searchKey: '',
    searchResult: []
  },

  onShow() {
    const user = getApp().globalData.userInfo
    if (!user) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
    this.loadRecommend()
  },

  async loadRecommend() {
    try {
      const list = await get('/second/list')
      console.log("商品列表：", list)
      this.setData({
        recommendGoods: list || []
      })
    } catch (e) {
      console.log("加载失败", e)
      this.setData({ recommendGoods: [] })
    }
  },

  onSearchInput(e) {
    const key = e.detail.value.trim()
    this.setData({ searchKey: key })
    if (!key) {
      this.setData({ searchResult: [] })
      return
    }
    this.doSearch()
  },

  async doSearch() {
    const key = this.data.searchKey
    try {
      const list = await get('/second/search', { keyword: key })
      this.setData({
        searchResult: list || []
      })
    } catch (e) {
      this.setData({ searchResult: [] })
    }
  },

  clearSearch() {
    this.setData({ searchKey: '', searchResult: [] })
  },

  // ✅ 查看更多：只跳转到广场页的闲置物品标签
  goList() {
    wx.switchTab({
      url: "/pages/news/list?from=index&type=second"
    })
  },

  // ✅ 点击商品：跳详情页（完全正常）
  goDetail(e) {
    const id = e.currentTarget.dataset.id
    console.log("✅ 正确商品ID =", id)

    if (!id) {
      wx.showToast({ title: 'ID不存在', icon: 'none' })
      return
    }

    wx.navigateTo({
      url: "/pages/second/detail?id=" + id
    })
  },

  // ✅ 点击分类：跳转到广软动态对应分类页面
  onCategoryTap(e) {
    const type = e.currentTarget.dataset.type
    console.log("✅ 分类类型 =", type)

    const app = getApp()
    app.globalData.categoryType = type

    if (type === 'second') {
      wx.switchTab({
        url: "/pages/news/list"
      })
    } else {
      wx.switchTab({
        url: "/pages/news/list"
      })
    }
  }
})