Page({
  data: {
    baseUrl: "http://127.0.0.1:8080/campus/file/",
    record: null,
    recordId: null
  },

  onLoad(options) {
    const recordId = options.recordId
    if (recordId) {
      this.data.recordId = recordId
      this.getRecord(recordId)
    }
  },

  getRecord(recordId) {
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/purchaseRecord/" + recordId,
      method: "GET",
      success: (res) => {
        wx.hideLoading()
        if (res.data.code === 200) {
          this.setData({ record: res.data.data })
        } else {
          wx.showToast({ title: "加载失败", icon: "none" })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: "网络错误", icon: "none" })
      }
    })
  },

  doPay() {
    const { recordId } = this.data
    if (!recordId) {
      wx.showToast({ title: "订单信息异常", icon: "none" })
      return
    }

    wx.showModal({
      title: "确认付款",
      content: "确定要支付此订单吗？",
      confirmText: "确认支付",
      cancelText: "稍后付款",
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({ title: "付款中..." })
          wx.request({
            url: "http://127.0.0.1:8080/campus/purchaseRecord/pay/" + recordId,
            method: "POST",
            success: (resp) => {
              wx.hideLoading()
              if (resp.data.code === 200) {
                wx.showToast({ title: "付款成功", icon: "success" })
                setTimeout(() => {
                  wx.switchTab({ url: "/pages/mine/mine" })
                }, 1500)
              } else {
                wx.showToast({ title: resp.data.msg || "付款失败", icon: "none" })
              }
            },
            fail: () => {
              wx.hideLoading()
              wx.showToast({ title: "网络错误", icon: "none" })
            }
          })
        } else {
          wx.showToast({ title: "已取消付款", icon: "none" })
          setTimeout(() => {
            wx.switchTab({ url: "/pages/mine/mine" })
          }, 1000)
        }
      }
    })
  },

  cancelPay() {
    wx.showModal({
      title: "取消付款",
      content: "确定要取消本次支付吗？可在订单列表中重新支付。",
      confirmText: "确定取消",
      cancelText: "继续付款",
      success: (res) => {
        if (res.confirm) {
          wx.showToast({ title: "已取消付款", icon: "none" })
          setTimeout(() => {
            wx.switchTab({ url: "/pages/mine/mine" })
          }, 1000)
        }
      }
    })
  }
})