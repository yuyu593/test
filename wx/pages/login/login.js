const { post } = require('../../utils/request')

Page({
  data: {
    studentNo: '',
    pwd: ''
  },

  setStudentNo(e) {
    this.setData({ studentNo: e.detail.value })
  },

  setPwd(e) {
    this.setData({ pwd: e.detail.value })
  },

  // 登录（修复版）
  async login() {
    const { studentNo, pwd } = this.data
    if (!studentNo || !pwd) {
      wx.showToast({ title: '学号密码不能为空', icon: 'none' })
      return
    }

    try {
      const user = await post('/user/login', {
        studentNo: studentNo,
        password: pwd
      })

      getApp().globalData.userInfo = user
      wx.setStorageSync('userInfo', user)

      wx.showToast({ title: '登录成功' })

      // 跳首页（不会再卡登录页）
      wx.switchTab({
        url: '/pages/index/index'
      })
    } catch (e) {
      wx.showToast({ title: '登录失败', icon: 'none' })
    }
  },

  // 注册
  async register() {
    const { studentNo, pwd } = this.data
    if (!studentNo || !pwd) {
      wx.showToast({ title: '不能为空', icon: 'none' })
      return
    }

    try {
      await post('/user/register', {
        studentNo,
        password: pwd,
        nickName: '校园用户'
      })
      wx.showToast({ title: '注册成功' })
    } catch (e) {
      wx.showToast({ title: '注册失败', icon: 'none' })
    }
  }
})