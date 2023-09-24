/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : ju_oj

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 24/09/2023 15:03:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` bigint NOT NULL COMMENT 'id',
  `user_id` bigint NOT NULL COMMENT '创建题目用户 id',
  `title` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '题目标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '题目内容',
  `tags` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签列表（json 数组）',
  `answer` text COLLATE utf8mb4_unicode_ci COMMENT '题目答案',
  `judge_case` text COLLATE utf8mb4_unicode_ci COMMENT '判题用例（json 数组）',
  `judge_config` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '判题配置（json 对象）',
  `submit_num` int NOT NULL DEFAULT '0' COMMENT '题目提交数',
  `ac_num` int NOT NULL DEFAULT '0' COMMENT '题目通过数',
  `thumb_num` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `favour_num` int NOT NULL DEFAULT '0' COMMENT '收藏数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

-- ----------------------------
-- Records of question
-- ----------------------------
BEGIN;
INSERT INTO `question` (`id`, `user_id`, `title`, `content`, `tags`, `answer`, `judge_case`, `judge_config`, `submit_num`, `ac_num`, `thumb_num`, `favour_num`, `create_time`, `update_time`, `is_delete`) VALUES (1702670367899848705, 1, 'A+B Problem', 'Description\n\nCalculate a+b\nInput\n\nTwo integer a,b (0<=a,b<=10)\nOutput:\n\nOutput a+b', '[\"简单\", \"模拟\"]', 'import java.io.*;\nimport java.util.*;\npublic class Main\n{\n            public static void main(String args[]) throws Exception\n            {\n                    Scanner cin=new Scanner(System.in);\n                    int a=cin.nextInt(),b=cin.nextInt();\n                    System.out.println(a+b);\n            }\n}', '[{\"input\":\"1 2\",\"output\":\"3\"},{\"input\":\"3 4\",\"output\":\"7\"}]', '{\"timeLimit\":1000,\"memoryLimit\":10000,\"stackLimit\":1000}', 2, 0, 0, 0, '2023-09-15 21:07:04', '2023-09-21 20:59:43', 0);
INSERT INTO `question` (`id`, `user_id`, `title`, `content`, `tags`, `answer`, `judge_case`, `judge_config`, `submit_num`, `ac_num`, `thumb_num`, `favour_num`, `create_time`, `update_time`, `is_delete`) VALUES (1704843524605755393, 1, 'A+B Problem', 'Description\n\nCalculate a+b\nInput\n\nTwo integer a,b (0<=a,b<=10)\nOutput:\n\nOutput a+b', '[\"简单\", \"模拟\"]', 'public class Main {\n    public static void main(String[] args) {\n        int a = Integer.parseInt(args[0]);\n        int b = Integer.parseInt(args[1]);\n        System.out.println(a + b);\n    }\n}', '[{\"input\":\"1 2\",\"output\":\"3\"},{\"input\":\"3 4\",\"output\":\"7\"}]', '{\"timeLimit\":1000,\"memoryLimit\":10000,\"stackLimit\":1000}', 10, 2, 0, 0, '2023-09-21 21:02:25', '2023-09-22 19:44:24', 0);
COMMIT;

-- ----------------------------
-- Table structure for question_submit_info
-- ----------------------------
DROP TABLE IF EXISTS `question_submit_info`;
CREATE TABLE `question_submit_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `question_id` bigint NOT NULL COMMENT '题目 id',
  `user_id` bigint NOT NULL COMMENT '创建用户 id',
  `judge_info` text COLLATE utf8mb4_unicode_ci COMMENT '判题信息（json 对象）',
  `submit_language` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编程语言',
  `submit_code` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户提交代码',
  `submit_state` int NOT NULL DEFAULT '0' COMMENT '判题状态（0 - 待判题、1 - 判题中、2 - AC、3 - Failed）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目提交信息表';

-- ----------------------------
-- Records of question_submit_info
-- ----------------------------
BEGIN;
INSERT INTO `question_submit_info` (`id`, `question_id`, `user_id`, `judge_info`, `submit_language`, `submit_code`, `submit_state`, `create_time`, `is_delete`) VALUES (10, 1704843524605755393, 1, '{\"message\":\"成功\",\"time\":67,\"memory\":1872}', 'java', 'public class Main\n{\n    public static void main(String args[]) throws Exception\n    {\n        int a = Integer.parseInt(args[0]);\n     int b = Integer.parseInt(args[1]);\n      System.out.println(a+b);\n    }\n}', 2, '2023-09-21 21:19:02', 0);
INSERT INTO `question_submit_info` (`id`, `question_id`, `user_id`, `judge_info`, `submit_language`, `submit_code`, `submit_state`, `create_time`, `is_delete`) VALUES (11, 1704843524605755393, 1, '{\"message\":\"成功\",\"time\":50,\"memory\":1819}', 'java', 'public class Main\n{\n    public static void main(String args[]) throws Exception\n    {\n        int a = Integer.parseInt(args[0]);\n     int b = Integer.parseInt(args[1]);\n      System.out.println(a+b);\n    }\n}', 2, '2023-09-22 19:34:54', 0);
INSERT INTO `question_submit_info` (`id`, `question_id`, `user_id`, `judge_info`, `submit_language`, `submit_code`, `submit_state`, `create_time`, `is_delete`) VALUES (12, 1704843524605755393, 1, '{\"message\":\"成功\",\"time\":52,\"memory\":3694}', 'java', 'public class Main\n{\n    public static void main(String args[]) throws Exception\n    {\n        int a = Integer.parseInt(args[0]);\n     int b = Integer.parseInt(args[1]);\n      System.out.println(a+b);\n    }\n}', 2, '2023-09-22 19:43:58', 0);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_account` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `user_password` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `salt` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '盐值',
  `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `user_avatar` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
  `user_profile` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户简介',
  `gender` tinyint DEFAULT '0' COMMENT '性别 1-男/0-女',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `user_state` tinyint NOT NULL DEFAULT '0' COMMENT '状态:0-正常/1-注销/2-封号',
  `user_role` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_account` (`user_account`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `user_account`, `user_password`, `salt`, `nickname`, `user_avatar`, `user_profile`, `gender`, `phone`, `email`, `user_state`, `user_role`, `create_time`, `update_time`, `is_delete`) VALUES (1, 'admin', '0299f7ca741ed6f4f4a215a378b1fe99', 'PbuvwE', 'OJ_nVBmUiZB', 'https://regengbaike.com/uploads/20230222/1bff61de34bdc7bf40c6278b2848fbcf.jpg', '该用户很懒，什么都没有写', 0, NULL, NULL, 0, 'admin', '2023-09-12 22:32:31', '2023-09-12 22:33:05', 0);
INSERT INTO `user` (`id`, `user_account`, `user_password`, `salt`, `nickname`, `user_avatar`, `user_profile`, `gender`, `phone`, `email`, `user_state`, `user_role`, `create_time`, `update_time`, `is_delete`) VALUES (5, 'demo', 'aa879c6f00e6b0faf0aa5742ea92b65d', 'MbQWHp', 'OJ_FQMFfmqp', 'https://regengbaike.com/uploads/20230222/1bff61de34bdc7bf40c6278b2848fbcf.jpg', '这里是测试修改用户信息的信息', 0, '123123123', NULL, 0, 'user', '2023-09-13 15:01:53', '2023-09-13 15:02:54', 0);
INSERT INTO `user` (`id`, `user_account`, `user_password`, `salt`, `nickname`, `user_avatar`, `user_profile`, `gender`, `phone`, `email`, `user_state`, `user_role`, `create_time`, `update_time`, `is_delete`) VALUES (6, 'demo2', '80a3da059d44dd378f0477e4108346ab', 'McloOF', 'OJ_OFoZKhuf', 'https://regengbaike.com/uploads/20230222/1bff61de34bdc7bf40c6278b2848fbcf.jpg', '该用户很懒，什么都没有写', 0, NULL, NULL, 0, 'user', '2023-09-13 16:45:38', '2023-09-13 16:52:45', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
