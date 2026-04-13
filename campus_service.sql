/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3306
 Source Schema         : campus_service

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 25/03/2026 15:55:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for campus_news
-- ----------------------------
DROP TABLE IF EXISTS `campus_news`;
CREATE TABLE `campus_news`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `publish_id` bigint NOT NULL COMMENT '发布者ID（0-管理员 其他-普通用户）',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资讯标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '资讯内容',
  `img_urls` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '资讯图片URL（多个用逗号分隔）',
  `type` int NULL DEFAULT 1 COMMENT '类型（1-校园通知 2-社团活动 3-生活贴士 4-校友动态）',
  `like_num` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` int NULL DEFAULT 0 COMMENT '状态（0-待审核 1-已发布 2-已下架）',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_publish_id`(`publish_id` ASC) USING BTREE COMMENT '发布者ID索引',
  INDEX `idx_type`(`type` ASC) USING BTREE COMMENT '类型索引',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '状态索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '校园资讯表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of campus_news
-- ----------------------------
INSERT INTO `campus_news` VALUES (1, 0, '春季运动会报名通知', '4月10日-20日可报名，地点操场', '', 1, 0, 1, 0, '2026-03-23 14:53:20', '2026-03-23 14:53:20');
INSERT INTO `campus_news` VALUES (2, 1, '社团招新啦', '计算机社团招新，每周五晚活动', '', 2, 0, 1, 0, '2026-03-23 14:53:20', '2026-03-23 14:53:20');

-- ----------------------------
-- Table structure for lost_found
-- ----------------------------
DROP TABLE IF EXISTS `lost_found`;
CREATE TABLE `lost_found`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '发布用户ID（关联user.id）',
  `type` int NOT NULL COMMENT '类型（1-寻物 2-拾物）',
  `goods_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物品名称',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '丢失/拾取地点',
  `happen_time` datetime NULL DEFAULT NULL COMMENT '丢失/拾取时间',
  `goods_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '物品描述',
  `img_urls` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '物品图片URL（多个用逗号分隔）',
  `status` int NULL DEFAULT 0 COMMENT '状态（0-待审核 1-已发布 2-已找回/认领 3-已下架）',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_type`(`type` ASC) USING BTREE COMMENT '类型索引',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '状态索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '失物招领信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lost_found
-- ----------------------------
INSERT INTO `lost_found` VALUES (1, 1, 1, '校园卡', '图书馆3楼', '2026-03-20 14:00:00', '卡号2023001，姓名张三', '', 1, 0, '2026-03-23 14:53:18', '2026-03-23 14:53:18');
INSERT INTO `lost_found` VALUES (2, 2, 2, '保温杯', '食堂2楼', '2026-03-21 12:00:00', '白色，带小熊图案', '', 1, 0, '2026-03-23 14:53:18', '2026-03-23 14:53:18');

-- ----------------------------
-- Table structure for second_hand
-- ----------------------------
DROP TABLE IF EXISTS `second_hand`;
CREATE TABLE `second_hand`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '发布用户ID（关联user.id）',
  `goods_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物品名称',
  `goods_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '物品分类（如：数码配件、代步工具）',
  `quality` int NULL DEFAULT 5 COMMENT '物品成色（1-全新 2-99新 3-9成新 4-8成新 5-一般 6-较差）',
  `price` decimal(10, 2) NOT NULL COMMENT '物品价格',
  `goods_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '物品描述',
  `img_urls` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '物品图片URL（多个用逗号分隔）',
  `trade_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '交易地点',
  `status` int NULL DEFAULT 0 COMMENT '状态（0-待审核 1-已发布 2-已交易 3-已下架）',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '状态索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引',
  INDEX `idx_goods_type`(`goods_type` ASC) USING BTREE COMMENT '物品分类索引'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '二手交易物品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of second_hand
-- ----------------------------
INSERT INTO `second_hand` VALUES (1, 1, '小米14手机', '数码配件', 2, 3500.00, '99新，无磕碰，配件齐全', '', '北门快递站', 1, 0, '2026-03-23 14:53:18', '2026-03-23 14:53:18');
INSERT INTO `second_hand` VALUES (2, 2, '山地自行车', '代步工具', 3, 800.00, '9成新，刚换轮胎', '', '宿舍楼下', 1, 0, '2026-03-23 14:53:18', '2026-03-23 14:53:18');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号（唯一）',
  `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '校园用户' COMMENT '用户昵称',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（建议加密存储）',
  `major` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '所属专业',
  `dorm_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '宿舍号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像URL',
  `credit_score` int NULL DEFAULT 100 COMMENT '信誉积分（初始100）',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_no`(`student_no` ASC) USING BTREE COMMENT '学号唯一索引',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '2023001', '张三', '123456', '计算机科学与技术', '', '', 100, '', 0, '2026-03-23 14:53:18', '2026-03-23 14:53:18');
INSERT INTO `user` VALUES (2, '2023002', '李四', '123456', '软件工程', '', '', 95, '', 0, '2026-03-23 14:53:18', '2026-03-23 14:53:18');

SET FOREIGN_KEY_CHECKS = 1;
