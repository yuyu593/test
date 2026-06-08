const { post, get } = require('../../utils/request')

Page({
  data: {
    baseUrl: "http://127.0.0.1:8080/campus/file/",
    detail: null,
    commentList: [],
    commentContent: "",
    currentUser: {
      userId: 1,
      nickname: "匿名用户",
      avatar: "default.png"
    },
    isCollected: false,
    targetType: 1,
    isLoading: false
  },

  onLoad(options) {
    const id = options.id;
    if (id) {
      this.getDetail(id);
    }
  },

  getDetail(id) {
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/second/detail/" + id,
      method: "GET",
      success: (res) => {
        console.log("详情数据", res.data)
        if (res.data.code === 200) {
          let detail = res.data.data;
          console.log("detail 完整数据：", detail)
          this.setData({
            detail: detail,
            currentUser: {
              userId: detail.userId || 1,
              nickname: detail.nickname || "匿名用户",
              avatar: detail.avatar || "default.png"
            }
          })
          this.refreshCollectStatus(detail.id)
          this.saveHistory()
        } else {
          wx.showToast({ title: "数据加载失败", icon: "none" })
        }
      },
      fail: () => {
        wx.showToast({ title: "请求失败", icon: "none" })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  async refreshCollectStatus(targetId) {
    const user = getApp().globalData.userInfo
    if (!user || !user.userId) {
      this.setData({ isCollected: false })
      return
    }
    try {
      const collectList = await get(`/collect/list/${user.userId}`)
      const isExist = collectList.some(item =>
        item.targetId === parseInt(targetId) && item.targetType === this.data.targetType
      )
      this.setData({ isCollected: isExist })
    } catch (err) {
      console.error("查询收藏状态失败", err)
      this.setData({ isCollected: false })
    }
  },

  async toggleCollect() {
    if (this.data.isLoading) return
    this.setData({ isLoading: true })

    const { detail, targetType } = this.data
    const targetId = detail.id
    const user = getApp().globalData.userInfo

    if (!user || !user.userId) {
      wx.showToast({ title: "请先登录", icon: "none" })
      this.setData({ isLoading: false })
      return
    }
    if (!targetId) {
      wx.showToast({ title: "数据异常", icon: "none" })
      this.setData({ isLoading: false })
      return
    }

    try {
      const res = await post('/collect/toggle', {
        userId: user.userId,
        targetId: targetId,
        targetType: targetType
      })
      await this.refreshCollectStatus(targetId)
      const tip = this.data.isCollected ? "收藏成功" : "已取消收藏"
      wx.showToast({ title: tip })
    } catch (err) {
      console.error("收藏操作异常", err)
      wx.showToast({ title: "操作失败", icon: "none" })
    } finally {
      this.setData({ isLoading: false })
    }
  },

  onInputComment(e) {
    this.setData({ commentContent: e.detail.value })
  },

  submitComment() {
    const content = this.data.commentContent.trim();
    if (!content) {
      wx.showToast({ title: "请输入评论", icon: "none" })
      return;
    }

    const user = this.data.currentUser;
    const newComment = {
      userId: user.userId,
      nickname: user.nickname,
      avatar: this.data.baseUrl + user.avatar,
      content: content,
      createTime: new Date().toISOString()
    };

    let commentList = this.data.commentList;
    commentList.unshift(newComment);

    this.setData({
      commentList: commentList,
      commentContent: ""
    });
    wx.showToast({ title: "发表成功" });
  },

  formatTime(timeStr) {
    if (!timeStr) return "";
    let date = new Date(timeStr);
    let year = date.getFullYear();
    let month = (date.getMonth() + 1).toString().padStart(2, '0');
    let day = date.getDate().toString().padStart(2, '0');
    let hour = date.getHours().toString().padStart(2, '0');
    let minute = date.getMinutes().toString().padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}`;
  },

  saveHistory() {
    const { detail, targetType } = this.data
    if (!detail || !detail.id) return

    const historyItem = {
      targetId: detail.id,
      targetType: targetType,
      // 关键：接口里的标题字段是 title，不是 goodsName
      title: detail.title || '',
      content: detail.goodsDesc || '',
      viewTime: this.formatTime(new Date())
    }

    let historyList = wx.getStorageSync('viewHistory') || []
    historyList = historyList.filter(item =>
      !(item.targetId === detail.id && item.targetType === targetType)
    )
    historyList.unshift(historyItem)

    if (historyList.length > 30) {
      historyList = historyList.slice(0, 30)
    }
    wx.setStorageSync('viewHistory', historyList)
    console.log("浏览记录已保存：", historyItem)
  }
})