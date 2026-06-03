const { post } = require('../../utils/request')

Page({
  data: {
    categoryList: ['校园活动', '生活分享', '求助问答', '其他'],
    tagList: ['#今日心情', '#校园糗事', '#上课日常', '#校园美食'],
    form: {
      title: '',
      category: '',
      type: null,
      content: '',
      imgUrls: [],
      tag: ''
    }
  },

  onTitleInput(e) {
    this.setData({
      'form.title': e.detail.value
    })
  },

  onCategoryChange(e) {
    const idx = e.detail.value
    const typeMap = [1, 2, 3, 4]
    this.setData({
      'form.category': this.data.categoryList[idx],
      'form.type': typeMap[idx]
    })
  },

  chooseImage() {
    wx.chooseImage({
      count: 3,
      success: (res) => {
        this.setData({
          'form.imgUrls': res.tempFilePaths
        })
      }
    })
  },

  onContentInput(e) {
    this.setData({
      'form.content': e.detail.value
    })
  },

  selectTag(e) {
    const tag = e.currentTarget.dataset.tag
    this.setData({
      'form.tag': tag
    })
  },

  async submit() {
    const app = getApp()
    const user = app.globalData.userInfo
    console.log('当前用户信息：', user)

    const { title, type, content, imgUrls } = this.data.form
    if (!user || !user.id) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    if (!title || !title.trim()) {
      wx.showToast({ title: '请填写动态标题', icon: 'none' })
      return
    }
    if (!type) {
      wx.showToast({ title: '请选择动态分类', icon: 'none' })
      return
    }
    if (!content || !content.trim()) {
      wx.showToast({ title: '请填写动态内容', icon: 'none' })
      return
    }

    wx.showLoading({ title: '发布中...' })

    try {
      let finalImgUrls = ''

      if (imgUrls && imgUrls.length > 0) {
        let urlList = []
        for (let path of imgUrls) {
          const uploadRes = await new Promise((resolve, reject) => {
            wx.uploadFile({
              url: 'http://127.0.0.1:8080/campus/upload',
              filePath: path,
              name: 'file',
              formData: { type: 'news' },
              success: resolve,
              fail: reject
            })
          })
          const uploadData = JSON.parse(uploadRes.data)
          if (uploadData.code !== 200) throw new Error(uploadData.msg)
          urlList.push(uploadData.data)
        }
        finalImgUrls = urlList.join(',')
      }

      const res = await post('/news/publish', {
        publishId: user.id,
        type: type,
        title: title,
        content: content,
        imgUrls: finalImgUrls
      })

      wx.hideLoading()
      console.log('发布成功：', res)
      wx.showToast({ title: '发布成功' })
      setTimeout(() => wx.navigateBack(), 1500)

    } catch (err) {
      wx.hideLoading()
      console.error('发布失败详情：', err)
      wx.showToast({ title: '发布失败', icon: 'error' })
    }
  }
})