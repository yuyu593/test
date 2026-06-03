const { post } = require('../../utils/request')

Page({
  data: {
    categoryList: ['寻物启事', '失物招领', '证件类', '电子产品', '生活用品', '学习用品', '其他'],
    form: {
      goodsName: '',
      category: '',
      type: null,
      address: '',
      phone: '',
      imgUrl: '',
      description: ''
    }
  },

  onNameInput(e) {
    this.setData({ 'form.goodsName': e.detail.value })
  },

  onCategoryChange(e) {
    const idx = e.detail.value
    const types = [1, 2, 1, 1, 2, 2, 1]
    this.setData({
      'form.category': this.data.categoryList[idx],
      'form.type': types[idx]
    })
  },

  onAddressInput(e) {
    this.setData({ 'form.address': e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ 'form.phone': e.detail.value })
  },

  onDescInput(e) {
    this.setData({ 'form.description': e.detail.value })
  },

  chooseImage() {
    wx.chooseImage({
      count: 1,
      success: (res) => {
        this.setData({ 'form.imgUrl': res.tempFilePaths[0] })
      }
    })
  },

  async submit() {
    const user = getApp().globalData.userInfo
    const { goodsName, type, address, phone, description, imgUrl } = this.data.form

    if (!user || !user.id) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    if (!goodsName) {
      wx.showToast({ title: '请输入物品名称', icon: 'none' })
      return
    }
    if (type == null) {
      wx.showToast({ title: '请选择分类', icon: 'none' })
      return
    }
    if (!address) {
      wx.showToast({ title: '请输入地点', icon: 'none' })
      return
    }
    if (!phone) {
      wx.showToast({ title: '请输入联系电话', icon: 'none' })
      return
    }
    if (!description) {
      wx.showToast({ title: '请输入描述', icon: 'none' })
      return
    }

    wx.showLoading({ title: '发布中...' })

    try {
      let finalImgUrl = ''

      if (imgUrl) {
        const uploadRes = await new Promise((resolve, reject) => {
          wx.uploadFile({
            url: 'http://127.0.0.1:8080/campus/upload',
            filePath: imgUrl,
            name: 'file',
            formData: { type: 'lost' },
            success: resolve,
            fail: reject
          })
        })

        const uploadData = JSON.parse(uploadRes.data)
        if (uploadData.code !== 200) throw new Error(uploadData.msg)
        finalImgUrl = uploadData.data
      }

      await post('/lost/publish', {
        userId: user.id,
        type: type,
        goodsName: goodsName,
        address: address,
        goodsDesc: description,
        imgUrls: finalImgUrl,
        phone: phone
      })

      wx.hideLoading()
      wx.showToast({ title: '发布成功' })
      setTimeout(() => wx.navigateBack(), 1500)
    } catch (err) {
      wx.hideLoading()
      console.error(err)
      wx.showToast({ title: '发布失败', icon: 'error' })
    }
  }
})