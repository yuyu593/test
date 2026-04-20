import { post } from '../../../utils/request';

Page({
  data: {
    goodsName: '',
    typeList: ['数码配件', '代步工具', '书籍资料', '生活用品', '其他'],
    currentType: '',
    qualityList: ['全新', '99新', '9成新', '8成新', '一般', '较差'],
    currentQuality: '',
    price: '',
    goodsDesc: '',
    tradeAddress: '',
    imgList: []
  },

  // 物品名称输入
  handleNameInput(e) {
    this.setData({ goodsName: e.detail.value });
  },

  // 分类选择
  handleTypeChange(e) {
    const index = e.detail.value;
    this.setData({ currentType: this.data.typeList[index] });
  },

  // 成色选择
  handleQualityChange(e) {
    const index = e.detail.value;
    this.setData({ 
      currentQuality: this.data.qualityList[index],
      qualityCode: index * 1 + 1 // 对应后端枚举值1-6
    });
  },

  // 价格输入
  handlePriceInput(e) {
    this.setData({ price: e.detail.value });
  },

  // 描述输入
  handleDescInput(e) {
    this.setData({ goodsDesc: e.detail.value });
  },

  // 交易地点输入
  handleAddressInput(e) {
    this.setData({ tradeAddress: e.detail.value });
  },

  // 选择图片
  chooseImage() {
    wx.chooseImage({
      count: 5 - this.data.imgList.length,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.setData({
          imgList: this.data.imgList.concat(res.tempFilePaths)
        });
      }
    });
  },

  // 提交发布
  async handleSubmit() {
    const { goodsName, currentType, qualityCode, price, goodsDesc, tradeAddress, imgList } = this.data;
    const userInfo = getApp().globalData.userInfo;

    // 校验参数
    if (!goodsName || !currentType || !price) {
      wx.showToast({
        title: '名称、分类、价格不能为空',
        icon: 'none'
      });
      return;
    }

    try {
      // TODO: 先上传图片到后端/OSS，获取图片URL
      const imgUrls = imgList.join(','); // 实际项目需替换为上传后的URL

      // 调用发布接口
      await post('/second/publish', {
        userId: userInfo.id,
        goodsName,
        goodsType: currentType,
        quality: qualityCode,
        price: parseFloat(price),
        goodsDesc,
        tradeAddress,
        imgUrls
      });

      wx.showToast({
        title: '发布成功，等待审核'
      });

      // 返回列表页
      wx.navigateBack();
    } catch (err) {
      console.error('发布失败：', err);
    }
  }
});