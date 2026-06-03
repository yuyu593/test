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
    // 点赞状态
    isLiked: false
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
        } else {
          wx.showToast({ title: "数据加载失败", icon: "none" })
        }
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  // 点赞/取消点赞（前端模拟）
  toggleLike() {
    const newState = !this.data.isLiked;
    this.setData({
      isLiked: newState
    });
    wx.showToast({
      title: newState ? "点赞成功" : "已取消点赞"
    });
    // 如需后端交互，可在此处调用 /news/like/{id} 接口
  },

  // 输入评论
  onInputComment(e) {
    this.setData({
      commentContent: e.detail.value
    })
  },

  // 发布评论（前端模拟）
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
  }
})