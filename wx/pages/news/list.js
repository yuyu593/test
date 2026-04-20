Page({
  data: {
    tab: 0 // 0:全部 1:求购中 2:失物招领
  },
  switchTab(e) {
    const t = Number(e.currentTarget.dataset.tab)
    this.setData({ tab: t })
  },
  // 跳转到详情页面
  goToDetail(e) {
    console.log('点击了卡片:', e.currentTarget.dataset);
    const { type, id } = e.currentTarget.dataset;
    
    // 根据类型跳转到不同的详情页面
    if (type === 'lost') {
      // 失物招领跳转到失物招领详情页面
      console.log('跳转到失物招领详情页面，ID:', id);
      wx.navigateTo({
        url: `/pages/lostFound/detail/detail?id=${id}`,
        success: function(res) {
          console.log('跳转成功:', res);
        },
        fail: function(res) {
          console.log('跳转失败:', res);
        }
      });
    } else if (type === 'second' || type === 'buy') {
      // 求购信息跳转到二手交易详情页面
      console.log('跳转到二手交易详情页面，ID:', id);
      wx.navigateTo({
        url: `/pages/secondHand/detail/detail?id=${id}`,
        success: function(res) {
          console.log('跳转成功:', res);
        },
        fail: function(res) {
          console.log('跳转失败:', res);
        }
      });
    } else if (type === 'post') {
      // 动态信息跳转到校园资讯详情页面
      console.log('跳转到校园资讯详情页面，ID:', id);
      wx.navigateTo({
        url: `/pages/campusNews/detail/detail?id=${id}`,
        success: function(res) {
          console.log('跳转成功:', res);
        },
        fail: function(res) {
          console.log('跳转失败:', res);
        }
      });
    }
  }
})