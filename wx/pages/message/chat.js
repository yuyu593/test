Page({
  data: {
      nickName: '',
      otherUserId: null,
      messages: [],
      draft: '',
      scrollTop: 0,
      nickNameFirst: '',
      itemType: '',
      itemId: null,
      itemTitle: ''
    },

  onLoad(options) {
    const otherUserId = options.otherUserId
    const nickName = decodeURIComponent(options.nickName || '')
    const itemType = options.itemType || ''
    const itemId = options.itemId || null
    const itemTitle = decodeURIComponent(options.itemTitle || '')
    if (!otherUserId) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      return
    }
    wx.setNavigationBarTitle({ title: nickName || '聊天' })
    this.setData({
      otherUserId: otherUserId,
      nickName: nickName || '聊天',
      nickNameFirst: (nickName || '对方').substring(0, 1),
      itemType: itemType,
      itemId: itemId,
      itemTitle: itemTitle
    })
    this.loadMessages()
  },

  onShow() {
    if (this.data.otherUserId) {
      this.loadMessages()
    }
  },

  loadMessages() {
    const userInfo = wx.getStorageSync('userInfo')
    const userId = wx.getStorageSync('userId') || (userInfo && (userInfo.userid || userInfo.userId))
    if (!userId) return
    const otherUserId = this.data.otherUserId
    wx.request({
      url: "http://127.0.0.1:8080/campus/message/conversation/" + userId + '/' + otherUserId,
      success: res => {
        if (res.data.code === 200) {
          const list = (res.data.data || []).map(item => {
            // 无论后端返回什么时间，一律用前端解析后显示
            const ct = String(item.createTime || '')
            item.timeStr = ct.substring(0, 19).replace('T', ' ')
            item.isMine = String(item.senderUserId) === String(userId)
            return item
          })
          this.setData({ messages: list })
          // 等视图层渲染完成后滚动到底部
          setTimeout(() => {
            const query = wx.createSelectorQuery().in(this)
            query.select('.chat-list').boundingClientRect(rect => {
              if (rect) this.setData({ scrollTop: rect.height + 10000 })
            }).exec()
          }, 300)
          // 标记对方发来的消息为已读
          wx.request({
            url: "http://127.0.0.1:8080/campus/message/read/conversation/" + userId + '/' + otherUserId,
            method: 'POST'
          })
        }
      }
    })
  },

  onDraftInput(e) {
    this.setData({ draft: e.detail.value })
  },

  onSend() {
    const content = (this.data.draft || '').trim()
    if (!content) {
      wx.showToast({ title: '请输入内容', icon: 'none' })
      return
    }
    const userInfo = wx.getStorageSync('userInfo')
    const userId = wx.getStorageSync('userId') || (userInfo && (userInfo.userid || userInfo.userId))
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    const myName = (userInfo && (userInfo.nickName || userInfo.nickname)) || '匿名用户'
    // 用前端本地时间，保证显示时间与用户本地一致
    const now = new Date()
    const pad = n => n < 10 ? '0' + n : n
    const localTime = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) + 'T' + pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds()) + '.' + String(now.getMilliseconds()).padStart(3, '0')
    const localTimeStr = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) + ' ' + pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds())

    // 先在本地显示，用户体验更流畅
    const messages = this.data.messages.slice()
    messages.push({
      messageId: 'local_' + Date.now(),
      isMine: true,
      content: content,
      createTime: localTime,
      timeStr: localTimeStr,
      itemType: this.data.itemType,
      itemId: this.data.itemId,
      itemTitle: this.data.itemTitle
    })
    this.setData({ messages: messages, draft: '' })
    // 立即滚动到底部
    setTimeout(() => {
      const query = wx.createSelectorQuery().in(this)
      query.select('.chat-list').boundingClientRect(rect => {
        if (rect) this.setData({ scrollTop: rect.height + 10000 })
      }).exec()
    }, 100)

    // 后端异步发送（不阻塞用户界面）
    wx.request({
      url: "http://127.0.0.1:8080/campus/message/send",
      method: 'POST',
      data: {
        senderUserId: userId,
        userId: this.data.otherUserId,
        type: 2,
        title: '来自' + myName + '的消息',
        content: content,
        itemType: this.data.itemType,
        itemId: this.data.itemId,
        itemTitle: this.data.itemTitle,
        createTime: localTime
      },
      success: res => {
        if (res.data.code === 200 && res.data.data && res.data.data.messageId) {
          // 把本地占位的 messageId 换成后端真实的 messageId
          const newMsgs = this.data.messages.slice()
          for (let i = 0; i < newMsgs.length; i++) {
            const id = String(newMsgs[i].messageId || '')
            if (id.indexOf('local_') === 0 && newMsgs[i].content === content) {
              newMsgs[i].messageId = res.data.data.messageId
            }
          }
          this.setData({ messages: newMsgs })
        }
      }
    })
  }
})
