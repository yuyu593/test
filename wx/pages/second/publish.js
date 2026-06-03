Page({
  data: {
    form: {
      goodsName: "",
      goodsType: "",
      price: "",
      goodsDesc: "",
      imgUrls: "",
      userId: null,
      tradeAddress: ""
    },
    categoryList: ["电子产品", "生活用品", "学习资料", "服饰", "其他"]
  },

  onLoad() {
    const user = getApp().globalData.userInfo
    if (user && user.id) {
      this.setData({ "form.userId": user.id })
    }
  },

  // 输入框绑定
  onGoodsName(e) { this.setData({ "form.goodsName": e.detail.value }) },
  onGoodsType(e) { this.setData({ "form.goodsType": this.data.categoryList[e.detail.value] }) },
  onPrice(e) { this.setData({ "form.price": e.detail.value }) },
  onTradeAddress(e) { this.setData({ "form.tradeAddress": e.detail.value }) },
  onGoodsDesc(e) { this.setData({ "form.goodsDesc": e.detail.value }) },

  // 选择图片
  chooseImage() {
    wx.chooseImage({
      count: 1,
      success: res => {
        this.setData({ "form.imgUrls": res.tempFilePaths[0] })
      }
    })
  },

  // ========== 发布按钮（最终修复版） ==========
  submit() {
    const { form } = this.data

    // 表单校验
    if (!form.userId) return wx.showToast({ title: "请先登录", icon: "none" })
    if (!form.goodsName) return wx.showToast({ title: "请输入物品名称", icon: "none" })
    if (!form.goodsType) return wx.showToast({ title: "请选择分类", icon: "none" })
    if (!form.price) return wx.showToast({ title: "请输入价格", icon: "none" })
    if (!form.goodsDesc) return wx.showToast({ title: "请输入描述", icon: "none" })
    if (!form.imgUrls) return wx.showToast({ title: "请上传图片", icon: "none" })

    wx.showLoading({ title: "发布中..." })

    // ====================== 上传图片 ======================
    wx.uploadFile({
      url: "http://127.0.0.1:8080/campus/upload",
      filePath: form.imgUrls,
      name: "file",
      formData: { type: "second" },

      success: (uploadRes) => {
        try {
          console.log("后端返回：", uploadRes.data)
          const uploadData = JSON.parse(uploadRes.data)

          if (uploadData.code !== 200) {
            throw new Error(uploadData.msg)
          }

          const imgUrl = uploadData.data

          // 提交表单
          const submitData = {
            ...form,
            imgUrls: imgUrl
          }

          wx.request({
            url: "http://127.0.0.1:8080/campus/second/publish",
            method: "POST",
            data: submitData,
            success: (res) => {
              if (res.data.code === 200) {
                wx.showToast({ title: "发布成功" })
                setTimeout(() => wx.navigateBack(), 1500)
              } else {
                wx.showToast({ title: res.data.msg || "发布失败", icon: "none" })
              }
            },
            fail: () => {
              wx.showToast({ title: "提交失败", icon: "none" })
            },
            complete: () => {
              wx.hideLoading()
            }
          })

        } catch (err) {
          wx.hideLoading()
          console.error("解析错误：", err)
          wx.showToast({ title: "图片上传失败", icon: "none" })
        }
      },

      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: "上传接口异常", icon: "none" })
      }
    })
  }
})