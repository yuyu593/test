Page({
  data: {
    baseUrl: "http://127.0.0.1:8080/campus/file/",
    form: {}
  },

  onLoad() {
    const user = getApp().globalData.userInfo || {}
    this.setData({ form: { ...user } })
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFile = res.tempFiles[0]
        this.uploadAvatar(tempFile.tempFilePath)
      }
    })
  },

  uploadAvatar(filePath) {
    wx.showLoading({ title: '上传中...' })
    wx.uploadFile({
      url: 'http://127.0.0.1:8080/campus/file/upload',
      filePath: filePath,
      name: 'file',
      success: (res) => {
        wx.hideLoading()
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            this.setData({ 'form.avatar': data.data })
            wx.showToast({ title: '头像上传成功', icon: 'success' })
          } else {
            wx.showToast({ title: data.msg || '上传失败', icon: 'none' })
          }
        } catch (e) {
          wx.showToast({ title: '上传失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '上传失败', icon: 'none' })
      }
    })
  },

  saveProfile() {
    if (!this.data.form.nickName) {
      wx.showToast({ title: '昵称不能为空', icon: 'none' })
      return
    }
    wx.showLoading({ title: '保存中...' })
    wx.request({
      url: 'http://127.0.0.1:8080/campus/user/update',
      method: 'PUT',
      data: this.data.form,
      success: (res) => {
        wx.hideLoading()
        if (res.data.code === 200) {
          // 更新全局用户信息和本地存储
          const user = this.data.form
          getApp().globalData.userInfo = user
          wx.setStorageSync('userInfo', user)
          wx.setStorageSync('userId', user.userId)
          wx.showToast({ title: '保存成功', icon: 'success' })
          setTimeout(() => {
            wx.navigateBack()
          }, 1000)
        } else {
          wx.showToast({ title: res.data.msg || '保存失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '保存失败', icon: 'none' })
      }
    })
  }
})
