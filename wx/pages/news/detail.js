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
    targetType: 3,  // 动态类型 type=3
    isLoading: false
  },

  onLoad(options) {
    const id = options.id;
    if (id) {
      this.getDetail(id);
    }
  },

  // 获取动态详情
  getDetail(id) {
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/news/detail?id=" + id,
      method: "GET",
      success: (res) => {
        console.log("动态详情", res.data)
        if (res.data.code === 200) {
          let detail = res.data.data;
          this.setData({
            detail: detail,
            currentUser: {
              userId: detail.publishId || 1,
              nickname: detail.nickname || "匿名用户",
              avatar: detail.avatar || "img.png"
            }
          })
          // 加载详情后，通过收藏列表刷新状态
          this.refreshCollectStatus(id)
          // 存入本地浏览记录
          this.saveHistory()
        } else {
          wx.showToast({ title: "数据加载失败", icon: "none" })
        }
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  // 统一使用收藏列表接口判断是否收藏（和失物页逻辑一致）
  async refreshCollectStatus(targetId) {
    const user = getApp().globalData.userInfo
    if (!user || !user.userId) {
      this.setData({ isCollected: false })
      return
    }
    try {
      const collectList = await get(`/collect/list/${user.userId}`)
      console.log("收藏列表", collectList)
      // 匹配 targetId + targetType
      const isExist = collectList.some(item =>
        item.targetId === parseInt(targetId) && item.targetType === this.data.targetType
      )
      this.setData({ isCollected: isExist })
    } catch (err) {
      console.error("刷新收藏状态失败", err)
      this.setData({ isCollected: false })
    }
  },

  // 收藏/取消收藏
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
      console.log("收藏接口返回：", res)

      // 操作完成，重新拉取收藏列表更新状态
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

  // 评论输入
  onInputComment(e) {
    this.setData({
      commentContent: e.detail.value
    })
  },

  // 发布评论
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

  // 时间格式化
  formatTime(timeStr) {
    if (!timeStr) return "";
    let date = new Date(timeStr);
    let year = date.getFullYear();
    let month = (date.getMonth() + 1).toString().padStart(2, '0');
    let day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  },

  // 存入本地浏览记录
  saveHistory() {
    const { detail, targetType } = this.data
    if (!detail || !detail.id) return

    // 当前浏览信息
    const historyItem = {
      targetId: detail.id,
      targetType: targetType,
      title: detail.title || '',
      content: detail.content || '',
      viewTime: this.formatTime(new Date()) // 格式化当前时间
    }

    // 读取原有记录
    let historyList = wx.getStorageSync('viewHistory') || []

    // 去重：同一条内容，删除旧记录，追加新记录
    historyList = historyList.filter(item =>
      !(item.targetId === detail.id && item.targetType === targetType)
    )
    // 最新记录插在头部
    historyList.unshift(historyItem)

    // 限制最大条数：最多存 30 条
    if (historyList.length > 30) {
      historyList = historyList.slice(0, 30)
    }

    // 写回本地缓存
    wx.setStorageSync('viewHistory', historyList)
  }
})