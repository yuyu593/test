Page({
  data: {
    message: {},
    replyContent: ''
  },

  onLoad(options) {
    this.setData({ messageId: options.messageId })
    this.loadDetail()
  },

  loadDetail() {
    const messageId = this.data.messageId
    const userId = wx.getStorageSync('userId') || (wx.getStorageSync('userInfo') && (wx.getStorageSync('userInfo').userid || wx.getStorageSync('userInfo').userId))
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/message/" + messageId,
      success: res => {
        if (res.data.code === 200) {
          const msg = res.data.data || {}
          msg.timeStr = (msg.createTime ? String(msg.createTime) : '').substring(0, 16).replace('T', ' ')
          // 系统通知：标题固定为"系统通知"，对方身份根据当前用户动态计算
          const isSystemNotice = msg.type === 1
          if (isSystemNotice) {
            msg.senderNickName = '系统通知'
            // 判断对方身份：当前用户与消息中 senderUserId 比较
            // 买家付款通知：msg.senderUserId = 卖家ID，msg.userId = 买家ID
            // 若当前用户是 senderUserId（卖家），则对方是 userId（买家）；否则对方是 senderUserId（卖家）
            let otherUserId = null
            let otherName = null
            if (msg.senderUserId && msg.userId) {
              if (String(userId) === String(msg.senderUserId)) {
                // 我是发件人（卖家通知场景的买家？其实存储的是交易对方ID）
                otherUserId = msg.userId
                otherName = msg.receiverUserName || '用户'
              } else {
                otherUserId = msg.senderUserId
                otherName = msg.senderUserName || '用户'
              }
            }
            msg.otherUserId = otherUserId
            msg.otherName = otherName
          }
          this.setData({ message: msg })
          if (msg.isRead === 0) {
            wx.request({
              url: "http://127.0.0.1:8080/campus/message/read/" + messageId,
              method: 'POST'
            })
          }
        } else {
          wx.showToast({ title: '加载失败', icon: 'none' })
        }
      },
      fail: () => wx.showToast({ title: '网络错误', icon: 'none' }),
      complete: () => wx.hideLoading()
    })
  },

  goChat() {
    const msg = this.data.message
    if (!msg || !msg.otherUserId) {
      wx.showToast({ title: '暂无可联系的对方', icon: 'none' })
      return
    }
    const nickName = msg.otherName || '用户'
    wx.navigateTo({
      url: '/pages/message/chat?otherUserId=' + msg.otherUserId + '&nickName=' + encodeURIComponent(nickName) + '&itemType=' + encodeURIComponent(msg.itemType || '') + '&itemId=' + (msg.itemId || '') + '&itemTitle=' + encodeURIComponent(msg.itemTitle || '')
    })
  },

  onReplyInput(e) {
    this.setData({ replyContent: e.detail.value })
  },

  sendReply() {
    const content = (this.data.replyContent || '').trim()
    if (!content) {
      wx.showToast({ title: '请输入回复内容', icon: 'none' })
      return
    }
    const userInfo = wx.getStorageSync('userInfo')
    const userId = wx.getStorageSync('userId') || (userInfo && (userInfo.userid || userInfo.userId))
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    const msg = this.data.message
    let senderUserId = msg.otherUserId || msg.senderUserId
    if (!senderUserId) {
      wx.showToast({ title: '系统消息不可回复', icon: 'none' })
      return
    }
    if (String(userId) === String(senderUserId)) {
      wx.showToast({ title: '不能回复自己', icon: 'none' })
      return
    }
    const senderName = (userInfo && (userInfo.nickName || userInfo.nickname)) || '匿名用户'
    const now = new Date()
    const pad = n => n < 10 ? '0' + n : n
    const localTime = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) + 'T' + pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds()) + '.' + String(now.getMilliseconds()).padStart(3, '0')

    wx.showLoading({ title: '发送中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/message/send",
      method: 'POST',
      data: {
        senderUserId: userId,
        userId: senderUserId,
        type: 2,
        title: '来自' + senderName + '的回复',
        content: content,
        createTime: localTime
      },
      success: res => {
        if (res.data.code === 200) {
          this.setData({ replyContent: '' })
          wx.showToast({ title: '回复成功', icon: 'success' })
        } else {
          wx.showToast({ title: '回复失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  }
})
