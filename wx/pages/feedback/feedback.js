// pages/feedback/feedback.js
const app = getApp()

Page({
  data: {
    // 反馈类型选项
    feedbackTypes: ['功能问题', '使用建议', '投诉举报', '其他'],
    selectedType: '',
    // 表单数据
    content: '',
    contact: '',
    // 提交按钮状态控制
    canSubmit: false
  },

  onLoad(options) {
    // 页面加载时可以做一些初始化
  },

  // 选择反馈类型
  onTypeChange(e) {
    const index = e.detail.value
    this.setData({
      selectedType: this.data.feedbackTypes[index]
    })
    this.checkCanSubmit()
  },

  // 输入反馈内容
  onContentInput(e) {
    this.setData({
      content: e.detail.value
    })
    this.checkCanSubmit()
  },

  // 输入联系方式
  onContactInput(e) {
    this.setData({
      contact: e.detail.value
    })
  },

  // 检查是否可以提交
  checkCanSubmit() {
    // 只要反馈内容不为空，即可提交
    const canSubmit = this.data.content.trim().length > 0
    this.setData({ canSubmit })
  },

  // 提交反馈
  submitFeedback() {
    if (!this.data.canSubmit) return

    const { selectedType, content, contact } = this.data
    const user = app.globalData.userInfo
    const userId = user?.userId

    // 校验登录状态
    if (!userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    // 构造请求数据
    const requestData = {
      type: selectedType,
      content: content,
      contact: contact
    }

    wx.showLoading({
      title: '提交中...'
    })

    // 发送请求
    wx.request({
      url: 'http://127.0.0.1:8080/campus/feedback/submit',
      method: 'POST',
      header: {
        'content-type': 'application/json',
        'userId': userId // 关键：带上请求头
      },
      data: requestData,
      success: (res) => {
        wx.hideLoading()
        if (res.data.code === 200) {
          wx.showToast({
            title: '提交成功',
            icon: 'success'
          })
          // 提交成功后，延迟返回上一页
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        } else {
          wx.showToast({
            title: res.data.msg || '提交失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        console.error('请求失败', err)
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
      }
    })
  }
})