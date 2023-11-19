# 数据库 && 接口设计



# 数据库设计

## 一、用户信息表

### **表设计**

|     字段      |   类型   |                 说明                 |
| :-----------: | :------: | :----------------------------------: |
|      id       |  bigint  |             主键id，自增             |
| user_account  | varchar  |           账号（唯一索引）           |
| user_password | varvhar  |             密码（加密）             |
|     salt      | varchar  |           盐值，给密码加密           |
|   nickname    | varchar  |                 昵称                 |
|  user_avatar  | varchar  |           用户头像图片地址           |
| user_profile  | varchar  |               用户简介               |
|    gender     | tinyint  |         性别（0-女 / 1-男）          |
|     phone     | varchar  |                 电话                 |
|     email     | varchar  |                 邮箱                 |
|  user_state   | tinyint  | 用户状态（0-正常 / 1-注销 / 2-封号） |
|   user_role   | varchar  |    用户角色（user 、admin、 ban）    |
|  create_time  | datetime |               创建时间               |
|  update_time  | datatime |               更新时间               |
|   is_delete   | tinyint  |             逻辑删除标志             |



### **数据库脚本**

```sql
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
```



## 二、题目表

### 表设计

题目id（设置为ASSIGN_ID，防止存在按照ID顺序爬取题目信息的情况）

创建人id

题目标题

题目内容：存放题目的内容，输入输出提示、描述、具体的详情

题目标签：栈、队列、链表、简单、中等、困难等

题目答案：管理员 / 用户 设置的标准答案

通过数

提交数：便于分析统计AC率

> 判题相关字段说明：
>
> 如果题目不是很复杂，用例文件不是很大的情况下，可以直接存放在数据库中；但是如果用例文件太大（>512KB），建议单独存放在一个文件中，数据库中只保存文件的URL（类似于用户头像存储）

题目用例（Json）：

- 输入用例：1, 2

- 输出用例：3

  ```json
  [
    {
  		"input": "1 2",
      "output": "3"
  	},
    {
  		"input": "3 4",
      "output": "7"
  	}
  ]
  ```

判题配置（Json）：

- 时间限制(ms)

- 内存限制(kb)

- （栈大小限制(kb)）

  ```json
  {
    "timeLimit": 1000,
    "memoryLimit": 1000,
    "stackLimit": 1000
  }
  ```

> 存Json的好处：便于扩展，只需要改变对象内部的字段，而不用修改数据库表
>
> 存Json的前提：
>
> 1. 不需要根据某个字段去倒查这条数据
> 2. 字段间含义相关，属于同一类值
> 3. 字段存储空间占用不大

点赞数

收藏数

|     字段     |  类型   |                           说明                           |
| :----------: | :-----: | :------------------------------------------------------: |
|      id      | bigint  |       主键、assign类型，防止用户按顺序爬取题目信息       |
|   user_id    | bigint  |                      创建题目用户id                      |
|    title     | vatchar |                         题目标题                         |
|   content    |  text   | 题目内容：存放题目的内容，输入输出提示、描述、具体的详情 |
|     tags     | varchar |  题目标签：栈、队列、链表、简单、中等、困难等，Json数组  |
|    answer    |  text   |                         题目答案                         |
|  judge_case  |  text   |                    判题用例，Json数组                    |
| judge_config |  text   |                    判题用例，Json对象                    |
|  submit_num  |   int   |                        题目提交数                        |
|    ac_num    |   int   |                        题目通过数                        |
|  thumb_num   |   int   |                          点赞数                          |
|  favour_num  |   int   |                          收藏数                          |
| create_time | datatime | 创建时间 |
|  update_time  | datatime |               更新时间               |
|   is_delete   | tinyint  |             逻辑删除标志             |



### 数据库脚本

```sql
CREATE TABLE IF NOT EXISTS question
(
  id          BIGINT COMMENT 'id' PRIMARY KEY,
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
```



## 三、题目提交信息表

### 表设计

提交信息id

提交用户id

题目id

编程语言：C++ / Java / Python ...

用户代码

判题状态：待判题、判题中、AC、Failed

判题信息（Json）：判题过程中得到的信息，比如程序失败的原因、执行的耗时、消耗的空间大小

```json
{
  "message": "程序执行信息",
  "time": 1000, // 消耗时间，单位为 ms
  "memory": 1000, // 消耗内存，单位为 kb
}
```

> message枚举值：
>
> - Accepted 成功
>
> - Wrong Answer 答案错误
>
> - Compile Error 编译错误
>
> - Memory Limit Exceeded 内存溢出
>
> - Time Limit Exceeded 超时
>
> - Presentation Error 展示错误
>
> - Output Limit Exceeded 输出溢出
>
> - Waiting 等待中
>
> - Dangerous Operation 危险操作
>
> - Runtime Error 运行错误（用户程序的问题）
>
> - System Error 系统错误（系统的问题）

|      字段       |  类型   |                 说明                 |
| :-------------: | :-----: | :----------------------------------: |
|       id        | bigint  |              主键、自增              |
|   question_id   | bigint  |                题目id                |
|     user_id     | bigint  |              提交用户id              |
|   judge_info    |  text   |          判题信息，Json对象          |
| submit_language | varchar |               编程语言               |
|   submit_code   |  text   |             用户提交代码             |
|  submit_state   |   int   | 提交状态（待判题、判题中、AC、失败） |
| create_time | datatime | 创建时间 |
|  update_time  | datatime |               更新时间               |
|   is_delete   | tinyint  |             逻辑删除标志             |



### 数据库脚本

```sql
CREATE TABLE IF NOT EXISTS question_submit_info
(
  id             BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
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
```

>### 小知识——数据库索引
>
>#### 什么情况下适合加索引？该给哪些字段加索引？
>
>答：首先从业务出发，无论是单个索引、还是联合索引，都要从你实际的查询语句、字段枚举值的区分度、字段的类型考虑（where 条件指定的字段），比如：`where userId = 1 and questionId = 2`。可以选择根据 userId 和 questionId 分别建立索引（需要分别根据这两个字段单独查询）；也可以选择给这两个字段建立联合索引（所查询的字段是绑定在一起的）。
>
>**原则**上：能不用索引就不用索引；能用单个索引就别用联合 / 多个索引；不要给没区分度的字段加索引（比如性别，就男 / 女），因为索引也是要占用空间的。



# 接口设计

汇总HTML版：[OJ-API](./api/oj-api.html)

OPEN API JSON：[open api json](./oj-openapi.json)

## 一、用户模块

详见[OpenAPI文档#user-module](./api/user-module.md)

## 二、题目模块

详见[OpenAPI文档#question-module](./api/question-module.md)

