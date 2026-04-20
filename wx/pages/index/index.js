const { get } = require('../../utils/request')

Page({
  data: {
    categories: [
      { icon: '📱', name: '数码配件', color: 'blue' },
      { icon: '🚲', name: '代步工具', color: 'green' },
      { icon: '📚', name: '学习书籍', color: 'orange' },
      { icon: '👕', name: '服饰美妆', color: 'purple' },
      { icon: '🎮', name: '游戏周边', color: 'pink' },
      { icon: '💡', name: '生活电器', color: 'teal' },
      { icon: '🎒', name: '运动户外', color: 'yellow' },
      { icon: '⋮⋮', name: '全部分类', color: 'grey' }
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

  // 加载推荐商品
  async loadRecommend() {
    const list = await get('/second/list')
    // 适配商品标签
    const goods = list.slice(0, 4).map((item, i) => ({
      ...item,
      quality: i % 2 === 0 ? '极品' : '99新',
      seller: i % 2 === 0 ? '大二学长' : '大四学姐'
    }))
    this.setData({ recommendGoods: goods })
  },

  // 搜索输入事件
  onSearchInput(e) {
    const key = e.detail.value.trim()
    this.setData({ searchKey: key })
    if (!key) {
      this.setData({ searchResult: [] })
      return
    }
    this.doSearch()
  },

  // 执行搜索
  async doSearch() {
    const key = this.data.searchKey
    try {
      const list = await get('/second/search', { keyword: key })
      const goods = list.map((item, i) => ({
        ...item,
        quality: '全新',
        seller: '校园用户'
      }))
      this.setData({ searchResult: goods })
    } catch (e) {
      this.setData({ searchResult: [] })
    }
  },

  // 清空搜索
  clearSearch() {
    this.setData({ searchKey: '', searchResult: [] })
  },

  // 跳转到全部商品页
  goList() {
    wx.switchTab({ url: '/pages/second/list' })
  }
})