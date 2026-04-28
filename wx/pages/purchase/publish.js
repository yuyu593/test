Page({
  data: {
    categoryList: ["数码产品", "学习资料", "生活用品", "服饰", "其他"],
    form: {
      userId: null,
      title: "",
      price: "",
      category: "",
      contact: "",
      content: "",
      isUrgent: 0
    }
  },

  onLoad() {
    // 读取用户信息（兼容两种key：userid 和 userId）
    const user = wx.getStorageSync('user') || wx.getStorageSync('userInfo');
    console.log("读取到的用户信息:", user);

    // 关键修复：兼容 userid 和 userId
    if (user && (user.userid || user.userId)) {
      const userId = user.userid || user.userId;
      this.setData({
        "form.userId": userId
      });
      console.log("已设置用户ID:", userId);
    } else {
      wx.showToast({ title: "请先登录", icon: "none" });
      setTimeout(() => wx.navigateBack(), 1500);
    }
  },

  onTitleInput(e) {
    this.setData({ "form.title": e.detail.value });
    console.log("当前标题:", this.data.form.title);
  },

  onPriceInput(e) {
    this.setData({ "form.price": e.detail.value });
  },

  onCategoryChange(e) {
    this.setData({
      "form.category": this.data.categoryList[e.detail.value]
    });
  },

  onContactInput(e) {
    this.setData({ "form.contact": e.detail.value });
  },

  onContentInput(e) {
    this.setData({ "form.content": e.detail.value });
  },

  setUrgent(e) {
    this.setData({
      "form.isUrgent": parseInt(e.currentTarget.dataset.status)
    });
  },

  submitPurchase() {
    const { form } = this.data;

    console.log("提交的表单数据:", form);

    if (!form.userId) {
      wx.showToast({ title: "用户ID为空，请重新登录", icon: "none" });
      return;
    }
    if (!form.title) {
      wx.showToast({ title: "请输入求购标题", icon: "none" });
      return;
    }
    if (!form.price) {
      wx.showToast({ title: "请输入预算", icon: "none" });
      return;
    }
    if (!form.category) {
      wx.showToast({ title: "请选择分类", icon: "none" });
      return;
    }
    if (!form.contact) {
      wx.showToast({ title: "请输入联系方式", icon: "none" });
      return;
    }
    if (!form.content) {
      wx.showToast({ title: "请输入物品描述", icon: "none" });
      return;
    }

    wx.showLoading({ title: "发布中..." });

    wx.request({
      url: "http://127.0.0.1:8080/campus/purchase/publish",
      method: "POST",
      data: form,
      success: (res) => {
        wx.hideLoading();
        console.log("后端返回:", res.data);
        if (res.data.code === 200) {
          wx.showToast({ title: "发布成功" });
          setTimeout(() => wx.navigateBack(), 1500);
        } else {
          wx.showToast({ title: res.data.msg || "发布失败", icon: "none" });
        }
      },
      fail: (err) => {
        wx.hideLoading();
        console.error("请求失败:", err);
        wx.showToast({ title: "网络异常", icon: "none" });
      }
    });
  }
})