Page({
  data: {
    baseUrl: "http://127.0.0.1:8080/campus/file/",
    detail: {},
    commentList: [],
    commentContent: "",
    showMsgModal: false,
    msgContent: ""
  },

  onLoad(options) {
    const id = options.id
    if (!id) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      return
    }
    this.getDetail(id)
  },

  getDetail(id) {
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/lost/detail/" + id,
      success: res => {
        wx.hideLoading()
        if (res.data.code === 200) {
          const d = res.data.data || {}
          if (d.img_urls && d.img_urls !== '') {
            const firstImg = d.img_urls.split(',')[0].trim()
            d.imgUrls = this.data.baseUrl + firstImg
          } else {
            d.imgUrls = ''
          }
          if (d.create_time) {
            d.createTime = String(d.create_time).substring(0, 16).replace('T', ' ')
          } else {
            d.createTime = ''
          }
          if (d.happenTime) {
            d.happenTime = String(d.happenTime).substring(0, 10).replace(/-/g, '/')
          } else {
            d.happenTime = ''
          }
          this.setData({ detail: d })
        } else {
          wx.showToast({ title: '加载失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  },

  formatTime(timeStr) {
    if (!timeStr) return ""
    let date = new Date(timeStr)
    let year = date.getFullYear()
    let month = (date.getMonth() + 1).toString().padStart(2, '0')
    let day = date.getDate().toString().padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  onInputComment(e) {
    this.setData({ commentContent: e.detail.value })
  },

  submitComment() {
    const content = this.data.commentContent.trim()
    if (!content) {
      wx.showToast({ title: "请输入评论", icon: "none" })
      return
    }
    const userInfo = wx.getStorageSync('userInfo')
    const nickname = (userInfo && (userInfo.nickName || userInfo.nickname)) || '匿名用户'
    const avatar = (userInfo && userInfo.avatar) || ''

    const newComment = {
      nickname: nickname,
      avatar: avatar ? this.data.baseUrl + avatar : '',
      content: content,
      createTime: this.formatTime(new Date().toISOString())
    }

    let list = this.data.commentList
    list.unshift(newComment)
    this.setData({ commentList: list, commentContent: "" })
    wx.showToast({ title: "发表成功" })
  },

  noop() {},

  showSendMsg() {
    const userInfo = wx.getStorageSync('userInfo')
    const userId = wx.getStorageSync('userId') || (userInfo && (userInfo.userid || userInfo.userId))
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    const detail = this.data.detail || {}
    if (!detail.publisherUserId) {
      wx.showToast({ title: '无法获取发布者信息', icon: 'none' })
      return
    }
    if (String(userId) === String(detail.publisherUserId)) {
      wx.showToast({ title: '不能联系自己', icon: 'none' })
      return
    }
    this.setData({ showMsgModal: true, msgContent: '' })
  },

  hideSendMsg() {
    this.setData({ showMsgModal: false, msgContent: '' })
  },

  onMsgInput(e) {
    this.setData({ msgContent: e.detail.value })
  },

  doSendMsg() {
    const content = (this.data.msgContent || '').trim()
    if (!content) {
      wx.showToast({ title: '请输入内容', icon: 'none' })
      return
    }
    const userInfo = wx.getStorageSync('userInfo')
    const userId = wx.getStorageSync('userId') || (userInfo && (userInfo.userid || userInfo.userId))
    const myName = (userInfo && (userInfo.nickName || userInfo.nickname)) || '匿名用户'
    const detail = this.data.detail || {}
    if (!detail.publisherUserId) {
      wx.showToast({ title: '无法获取发布者信息', icon: 'none' })
      return
    }

    wx.showLoading({ title: '发送中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/message/send",
      method: 'POST',
      data: {
        senderUserId: userId,
        userId: detail.publisherUserId,
        type: 2,
        title: '来自' + myName + '的消息',
        content: content,
        itemType: 'lost',
        itemId: detail.id,
        itemTitle: detail.goodsName
      },
      success: res => {
        wx.hideLoading()
        if (res.data.code === 200) {
          this.setData({ showMsgModal: false, msgContent: '' })
          wx.showToast({ title: '发送成功', icon: 'success' })
          wx.navigateTo({
            url: '/pages/message/chat?otherUserId=' + detail.publisherUserId +
                 '&nickName=' + encodeURIComponent(detail.nickname || '发布者') +
                 '&itemType=lost&itemId=' + detail.id +
                 '&itemTitle=' + encodeURIComponent(detail.goodsName || '')
          })
        } else {
          wx.showToast({ title: (res.data.msg || res.data.message) || '发送失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  }
})
