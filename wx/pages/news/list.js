Page({
  data: {
    tab: 0 // 0:全部 1:求购中 2:失物招领
  },
  switchTab(e) {
    const t = Number(e.currentTarget.dataset.tab)
    this.setData({ tab: t })
  }
})