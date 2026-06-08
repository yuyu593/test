Page({
  data: {
    baseUrl: "http://127.0.0.1:8080/campus/file/",
    currentTab: "",
    list: []
  },

  onLoad() {
    this.getList()
  },

  switchTab(e) {
    const type = e.currentTarget.dataset.type
    this.setData({
      currentTab: type,
      list: []
    }, () => {
      this.getList()
    })
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    const type = e.currentTarget.dataset.type;

    let url = "";
    switch (type) {
      case "news":
        url = "/pages/news/detail?id=" + id; break;
      case "lost":
        url = "/pages/lost/detail?id=" + id; break;
      case "purchase":
        url = "/pages/purchase/detail?id=" + id; break;
      case "second":
        url = "/pages/second/detail?id=" + id; break;
      default:
        url = "/pages/second/detail?id=" + id;
    }
    wx.navigateTo({ url });
  },

  getList() {
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/square/list",
      method: "GET",
      data: { type: this.data.currentTab },
      success: (res) => {
        if (res.data.code === 200) {
          const list = res.data.data.map((item, index) => {
            item.uniqueId = "item_" + index;
            let imgList = [];
            if (item.img_urls) {
              imgList = item.img_urls.split(",").map(i => i.trim()).filter(i => i);
            }
            item.imgList = imgList;
            return item;
          });
          this.setData({ list });
        }
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  formatTime(timeStr) {
    if (!timeStr) return ""
    const now = new Date()
    const time = new Date(timeStr)
    const diff = (now - time) / 1000 / 60
    if (diff < 60) return Math.floor(diff) + "分钟前"
    if (diff < 1440) return Math.floor(diff / 60) + "小时前"
    return Math.floor(diff / 1440) + "天前"
  }
})