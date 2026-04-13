const { post } = require('../../utils/request')
Page({
  data: {
    name: '', type: '', price: '', desc: '', address: ''
  },

  submit() {
    const user = getApp().globalData.userInfo
    post('/second/publish', {
      userId: user.id,
      goodsName: this.data.name,
      goodsType: this.data.type,
      price: this.data.price,
      goodsDesc: this.data.desc,
      tradeAddress: this.data.address
    }).then(() => {
      wx.showToast({ title: '发布成功' })
      wx.navigateBack()
    })
  }
})