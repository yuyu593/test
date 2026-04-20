import { get } from '../../../utils/request';

Page({
  data: {
    lostList: [],
    typeList: ['全部分类', '寻物', '拾物'],
    currentType: '',
    type: 0,
    address: ''
  },

  onLoad() {
    this.getLostList();
  },

  // 获取失物招领列表
  async getLostList() {
    try {
      const { type, address } = this.data;
      const res = await get('/lost/list', {
        type: type || undefined,
        address: address || undefined
      });
      
      this.setData({
        lostList: res
      });
    } catch (err) {
      console.error('获取列表失败：', err);
    }
  },

  // 分类选择
  handleTypeChange(e) {
    const index = e.detail.value;
    this.setData({
      currentType: this.data.typeList[index],
      type: index === 0 ? 0 : index
    });
  },

  // 地点输入
  handleAddressInput(e) {
    this.setData({
      address: e.detail.value
    });
  },

  // 筛选查询
  handleSearch() {
    this.getLostList();
  },

  // 跳转到发布页
  gotoPublish() {
    wx.navigateTo({
      url: '/pages/lostFound/publish/publish'
    });
  }
});