// pages/lostFound/detail/detail.js
import { get, post } from '../../../utils/request';

Page({
  data: {
    postData: {},
    userInfo: {},
    imageList: [],
    comments: [],
    commentContent: '',
    likeCount: 0,
    commentCount: 0,
    isLiked: false,
    isCollected: false
  },

  onLoad(options) {
    const id = options.id;
    if (id) {
      this.loadPostDetail(id);
      this.loadComments(id);
    }
  },

  // 加载帖子详情
  async loadPostDetail(id) {
    const userInfo = getApp().globalData.userInfo || {};
    // 先显示默认数据，避免页面空白
    const defaultPostData = {
      goodsName: '失物招领',
      address: '校园内',
      goodsDesc: '这是一条失物招领信息',
      createTime: new Date().toLocaleString(),
      likeNum: 0,
      commentNum: 0
    };
    
    this.setData({
      postData: defaultPostData,
      userInfo,
      imageList: [],
      likeCount: 0,
      commentCount: 0
    });
    
    try {
      console.log('开始加载帖子详情，ID:', id);
      const postData = await get('/lost/detail', { id });
      console.log('加载帖子详情成功:', postData);
      const imageList = postData.goodsImg ? postData.goodsImg.split(',') : [];
      
      this.setData({
        postData,
        userInfo,
        imageList,
        likeCount: postData.likeNum || 0,
        commentCount: postData.commentNum || 0
      });
    } catch (err) {
      console.error('加载帖子详情失败：', err);
      // 加载失败时，保持默认数据
    }
  },

  // 加载评论
  async loadComments(postId) {
    try {
      // 模拟评论数据
      const comments = [
        {
          id: 1,
          nickName: '小明',
          avatar: 'https://picsum.photos/seed/user1/100/100',
          content: '希望你能尽快找到失物！',
          createTime: '2026-03-25 10:00:00'
        },
        {
          id: 2,
          nickName: '小红',
          avatar: 'https://picsum.photos/seed/user2/100/100',
          content: '我昨天在图书馆看到过类似的物品，可能在失物招领处',
          createTime: '2026-03-25 11:30:00'
        }
      ];
      
      this.setData({
        comments,
        commentCount: comments.length
      });
    } catch (err) {
      console.error('加载评论失败：', err);
    }
  },

  // 预览图片
  previewImage(e) {
    const index = e.currentTarget.dataset.index;
    const imageList = this.data.imageList;
    
    wx.previewImage({
      current: imageList[index],
      urls: imageList
    });
  },

  // 点赞
  async handleLike() {
    try {
      const { postData, isLiked, likeCount } = this.data;
      
      // 模拟点赞操作
      const newLikeCount = isLiked ? likeCount - 1 : likeCount + 1;
      
      this.setData({
        isLiked: !isLiked,
        likeCount: newLikeCount
      });
      
      wx.showToast({
        title: isLiked ? '取消点赞' : '点赞成功',
        icon: 'none'
      });
    } catch (err) {
      console.error('点赞失败：', err);
    }
  },

  // 收藏
  async handleCollect() {
    try {
      const { isCollected } = this.data;
      
      this.setData({
        isCollected: !isCollected
      });
      
      wx.showToast({
        title: isCollected ? '取消收藏' : '收藏成功',
        icon: 'none'
      });
    } catch (err) {
      console.error('收藏失败：', err);
    }
  },

  // 聚焦评论输入框
  focusComment() {
    this.setData({
      commentFocus: true
    });
  },

  // 评论输入
  handleCommentInput(e) {
    this.setData({
      commentContent: e.detail.value
    });
  },

  // 提交评论
  async handleSubmitComment() {
    const { commentContent, postData } = this.data;
    const userInfo = getApp().globalData.userInfo;
    
    if (!commentContent.trim()) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      });
      return;
    }
    
    try {
      // 模拟评论提交
      const newComment = {
        id: Date.now(),
        nickName: userInfo.nickName || '校园用户',
        avatar: userInfo.avatar || 'https://picsum.photos/seed/user/100/100',
        content: commentContent,
        createTime: new Date().toLocaleString()
      };
      
      const comments = [...this.data.comments, newComment];
      
      this.setData({
        comments,
        commentCount: comments.length,
        commentContent: ''
      });
      
      wx.showToast({
        title: '评论成功',
        icon: 'none'
      });
    } catch (err) {
      console.error('评论失败：', err);
      wx.showToast({
        title: '评论失败，请重试',
        icon: 'none'
      });
    }
  }
});