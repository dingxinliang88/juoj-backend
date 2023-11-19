CREATE DATABASE ju_oj;
USE ju_oj;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id' ,
  user_account  VARCHAR(32) NOT NULL UNIQUE COMMENT '账号' ,
  user_password VARCHAR(64) NOT NULL COMMENT '密码',
  salt          VARCHAR(16) NOT NULL COMMENT '盐值',
  nickname     VARCHAR(32) DEFAULT NULL COMMENT '用户昵称',
  user_avatar   VARCHAR(512) DEFAULT NULL COMMENT '用户头像',
  user_profile  VARCHAR(512) DEFAULT NULL COMMENT '用户简介',
  gender       TINYINT DEFAULT 0 NULL COMMENT '性别 1-男/0-女',
  phone        VARCHAR(16) DEFAULT NULL COMMENT '电话',
  email        VARCHAR(64) DEFAULT NULL COMMENT '邮箱',
  user_state    TINYINT DEFAULT '0' NOT NULL COMMENT '状态:0-正常/1-注销/2-封号',
  user_role     VARCHAR(32) DEFAULT 'user' NOT NULL COMMENT '用户角色：user/admin/ban',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_delete     TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除'
) COMMENT '用户表' COLLATE = utf8mb4_unicode_ci;

-- 题目表
CREATE TABLE IF NOT EXISTS question
(
  id           BIGINT  			COMMENT 'id' PRIMARY KEY,
  user_id      BIGINT             NOT NULL COMMENT '创建题目用户 id',
  title       VARCHAR(64)        NULL COMMENT '题目标题',
  content     TEXT               NULL COMMENT '题目内容',
  tags        VARCHAR(128)       NULL COMMENT '标签列表（json 数组）',
  answer      TEXT               NULL COMMENT '题目答案',
  judge_case   TEXT               NULL COMMENT '判题用例（json 数组）',
  judge_config VARCHAR(512)        NULL COMMENT '判题配置（json 对象）',
  submit_num   INT                DEFAULT 0     NOT NULL COMMENT '题目提交数',
  ac_num       INT                DEFAULT 0     NOT NULL COMMENT '题目通过数',
  thumb_num    INT                DEFAULT 0     NOT NULL COMMENT '点赞数',
  favour_num   INT                DEFAULT 0     NOT NULL COMMENT '收藏数',
  create_time  DATETIME          DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
  update_time  DATETIME          DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_delete    TINYINT           DEFAULT 0     NOT NULL COMMENT '是否删除',
  INDEX idx_user_id (user_id)
) COMMENT = '题目表' COLLATE = utf8mb4_unicode_ci;

-- 题目提交信息表
CREATE TABLE IF NOT EXISTS question_submit_info
(
  id              BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
  question_id     BIGINT             NOT NULL COMMENT '题目 id',
  user_id         BIGINT             NOT NULL COMMENT '提交用户 id',
  judge_info      TEXT               NULL COMMENT '判题信息（json 对象）',
  submit_language VARCHAR(32)       NOT NULL COMMENT '编程语言',
  submit_code     TEXT               NOT NULL COMMENT '用户提交代码',
  submit_state    INT                DEFAULT 0     NOT NULL COMMENT '判题状态（0 - 待判题、1 - 判题中、2 - AC、3 - Failed）',
  create_time     DATETIME          DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
  update_time     DATETIME          DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_delete       TINYINT           DEFAULT 0     NOT NULL COMMENT '是否删除',
  INDEX idx_question_id (question_id),
  INDEX idx_user_id (user_id)
) COMMENT = '题目提交信息表' COLLATE = utf8mb4_unicode_ci;