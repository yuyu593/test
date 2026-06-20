Page({
  data: { chats: [] },

  onShow() { this.loadMessages() },

  loadMessages() {
    const userInfo = wx.getStorageSync('userInfo')
    const userId = wx.getStorageSync('userId') || (userInfo && (userInfo.userid || userInfo.userId))
    if (!userId) return
    const uid = String(userId)
    wx.request({
      url: "http://127.0.0.1:8080/campus/message/user/" + userId,
      success: res => {
        if (res.data.code === 200) {
          const list = res.data.data || []
          const map = {}
          for (let i = 0; i < list.length; i++) {
            const item = list[i]
            let key
            let chatOtherUserId
            let chatOtherName
            let isSystem = false
            // type===1 表示系统通知（付款/购买通知），标题统一显示"系统通知"
            if (item.type === 1) {
              key = 'system_' + item.messageId
              isSystem = true
              // 对方用户ID和昵称：优先用后端计算好的 counterpartyUserId/counterpartyName
              chatOtherUserId = item.counterpartyUserId
              chatOtherName = item.counterpartyName || '用户'
            } else if (String(item.senderUserId) === uid) {
              // 我发出的消息 → 对方是接收者
              key = 'user_' + item.userId
              chatOtherUserId = item.userId
              chatOtherName = item.receiverNickName || item.senderNickName || '用户'
            } else {
              // 对方发给我的消息 → 对方是发送者
              key = 'user_' + item.senderUserId
              chatOtherUserId = item.senderUserId
              chatOtherName = item.senderNickName || '用户'
            }
            if (!map[key]) {
              let content = item.content || item.title || ''
              if (String(item.senderUserId) !== uid && item.title && item.content && item.title.indexOf('来自') === 0) {
                // 对方发来的消息，去掉"来自xx的消息"前缀
                content = item.content
              }
              map[key] = {
                key: key,
                otherUserId: chatOtherUserId,
                otherName: chatOtherName,
                nickName: isSystem ? '系统通知' : chatOtherName,
                isSystem: isSystem,
                lastContent: content,
                lastTime: item.createTime,
                unreadCount: 0,
                messageId: item.messageId
              }
            }
            // 后续消息只累加未读计数
            if (item.isRead === 0 && String(item.userId) === uid) {
              map[key].unreadCount++
            }
          }
          const chats = Object.values(map).sort((a, b) => (b.lastTime || '').localeCompare(a.lastTime || ''))
          let totalUnread = 0
          chats.forEach(c => {
            if (c.unreadCount > 0) totalUnread += c.unreadCount
            if (c.lastTime) {
              c.lastTime = String(c.lastTime).substring(0, 16).replace('T', ' ')
            }
          })
          this.setData({ chats })
          if (totalUnread > 0) {
            wx.setTabBarBadge({
              index: 3,
              text: totalUnread > 99 ? '99+' : String(totalUnread)
            })
          } else {
            wx.removeTabBarBadge({ index: 3 })
          }
        }
      }
    })
  },

  goDetail(e) {
    const item = e.currentTarget.dataset.item
    if (!item) return
    if (item.isSystem) {
      if (!item.messageId) {
        wx.showToast({ title: '消息数据异常', icon: 'none' })
        return
      }
      wx.navigateTo({ url: '/pages/message/detail?messageId=' + item.messageId })
    } else {
      if (!item.otherUserId) {
        wx.showToast({ title: '用户信息异常', icon: 'none' })
        return
      }
      wx.navigateTo({
        url: '/pages/message/chat?otherUserId=' + item.otherUserId + '&nickName=' + encodeURIComponent(item.otherName || '用户')
      })
    }
  },

  goChat(e) {
    // 系统通知上的"立即联系"快捷按钮，直接跳聊天
    e.stopPropagation && e.stopPropagation()
    const item = e.currentTarget.dataset.item
    if (!item) return
    if (!item.otherUserId) {
      wx.showToast({ title: '暂无可联系的对方', icon: 'none' })
      return
    }
    wx.navigateTo({
      url: '/pages/message/chat?otherUserId=' + item.otherUserId + '&nickName=' + encodeURIComponent(item.otherName || '用户')
    })
  },

  markAllRead() {
    const userInfo = wx.getStorageSync('userInfo')
    const userId = wx.getStorageSync('userId') || (userInfo && (userInfo.userid || userInfo.userId))
    if (!userId) return
    wx.request({
      url: "http://127.0.0.1:8080/campus/message/read/all/" + userId,
      method: 'POST',
      success: () => {
        this.loadMessages()
        wx.showToast({ title: '已全部标为已读', icon: 'success' })
      }
    })
  }
})
