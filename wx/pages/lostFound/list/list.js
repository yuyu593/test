import { get } from '../../../utils/request';

Page({
  data: {
    goodsList: [],
    typeList: ['数码配件', '代步工具', '书籍资料', '生活用品', '其他'],
    currentType: '',
    minPrice: '',
    maxPrice: ''
  },

  onLoad() {
    this.getGoodsList();
  },

  // 获取二手物品列表
  async getGoodsList() {
    try {
      const { currentType, minPrice, maxPrice } = this.data;
      const res = await get('/second/list', {
        goodsType: currentType,
        minPrice: minPrice || undefined,
        maxPrice: maxPrice || undefined
      });
      
      this.setData({
        goodsList: res
      });
    } catch (err) {
      console.error('获取列表失败：', err);
    }
  },

  // 分类选择
  handleTypeChange(e) {
    const index = e.detail.value;
    this.setData({
      currentType: this.data.typeList[index]
    });
  },

  // 最低价格输入
  handleMinPriceInput(e) {
    this.setData({
      minPrice: e.detail.value
    });
  },

  // 最高价格输入
  handleMaxPriceInput(e) {
    this.setData({
      maxPrice: e.detail.value
    });
  },

  // 筛选查询
  handleSearch() {
    this.getGoodsList();
  },

  // 跳转到发布页
  gotoPublish() {
    wx.navigateTo({
      url: '/pages/secondHand/publish/publish'
    });
  }
});