Page({
  data: {
    baseUrl: "http://127.0.0.1:8080/campus/file/",
    detail: {}, // 用空对象，避免 wx:if 不渲染
    commentList: [],
    commentContent: "",
    currentUser: {
      userId: 1,
      nickname: "匿名用户",
      avatar: ""
    }
  },

  onLoad(options) {
    console.log("跳转参数:", options);
    const id = options.id;
    if (id) {
      this.getDetail(id);
    } else {
      wx.showToast({ title: "参数错误", icon: "none" });
    }
  },

  getDetail(id) {
    wx.showLoading({ title: '加载中...' })
    wx.request({
      url: "http://127.0.0.1:8080/campus/purchase/detail?id=" + id,
      method: "GET",
      success: (res) => {
        console.log("求购详情返回:", res.data);
        if (res.data.code === 200) {
          let detail = res.data.data;
          this.setData({
            detail: detail || {}, // 防止后端返回 null
            currentUser: {
              userId: detail?.userId || 1,
              nickname: detail?.nickname || "匿名用户",
              avatar: detail?.avatar || ""
            }
          });
        } else {
          wx.showToast({ title: "加载失败", icon: "none" });
        }
      },
      fail: () => {
        wx.showToast({ title: "网络请求失败", icon: "none" });
      },
      complete: () => {
        wx.hideLoading();
      }
    })
  },
  
  toggleCollect() {
    const newState = !this.data.isCollected;
    this.setData({
      isCollected: newState
    });
    wx.showToast({
      title: newState ? "收藏成功" : "已取消收藏"
    });
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
    return `${year}-${month}-${day}`;
  }
})