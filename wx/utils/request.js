const app = getApp()
function request(url, method = 'GET', data = {}) {
  wx.showLoading({ title: '加载中' })
  return new Promise((resolve, reject) => {
    wx.request({
      url: app.globalData.baseUrl + url,
      method,
      data,
      header: { 'Content-Type': 'application/json' },
      success: res => {
        wx.hideLoading()
        const { code, data, msg } = res.data
        if (code === 200) resolve(data)
        else { wx.showToast({ title: msg, icon: 'none' }); reject(msg) }
      },
      fail: () => { wx.hideLoading(); wx.showToast({ title: '网络异常', icon: 'none' }) }
    })
  })
}
module.exports = { get: (u, d) => request(u, 'GET', d), post: (u, d) => request(u, 'POST', d) }