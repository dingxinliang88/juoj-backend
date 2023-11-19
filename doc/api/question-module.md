# Ju-OJ接口文档


**简介**:Ju-OJ接口文档


**HOST**:localhost:8080

**联系人**: CodeJuzi (codejuzi@163.com)


**Version**:1.0


**接口路径**:/v2/api-docs


[TOC]


# question-controller


## 添加题目


**接口地址**:`/api/question/add`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "answer": "",
  "content": "",
  "judgeCases": [
    {
      "input": "",
      "output": ""
    }
  ],
  "judgeConfig": {
    "memoryLimit": 0,
    "stackLimit": 0,
    "timeLimit": 0
  },
  "tags": [],
  "title": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionAddRequest|questionAddRequest|body|true|QuestionAddRequest|QuestionAddRequest|
|&emsp;&emsp;answer|||false|string||
|&emsp;&emsp;content|||false|string||
|&emsp;&emsp;judgeCases|||false|array|JudgeCase|
|&emsp;&emsp;&emsp;&emsp;input|||false|string||
|&emsp;&emsp;&emsp;&emsp;output|||false|string||
|&emsp;&emsp;judgeConfig|||false|JudgeConfig|JudgeConfig|
|&emsp;&emsp;&emsp;&emsp;memoryLimit|||false|integer||
|&emsp;&emsp;&emsp;&emsp;stackLimit|||false|integer||
|&emsp;&emsp;&emsp;&emsp;timeLimit|||false|integer||
|&emsp;&emsp;tags|||false|array|string|
|&emsp;&emsp;title|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«long»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||integer(int64)|integer(int64)|
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": 0,
	"message": ""
}
```


## 删除题目


**接口地址**:`/api/question/delete`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "id": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|deleteRequest|deleteRequest|body|true|DeleteRequest|DeleteRequest|
|&emsp;&emsp;id|||false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«boolean»|
|204|No Content||
|401|Unauthorized||
|403|Forbidden||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||boolean||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": true,
	"message": ""
}
```


## 用户编辑题目


**接口地址**:`/api/question/edit`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "answer": "",
  "content": "",
  "id": 0,
  "judgeCases": [
    {
      "input": "",
      "output": ""
    }
  ],
  "judgeConfig": {
    "memoryLimit": 0,
    "stackLimit": 0,
    "timeLimit": 0
  },
  "tags": [],
  "title": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionUpdateRequest|questionUpdateRequest|body|true|QuestionUpdateRequest|QuestionUpdateRequest|
|&emsp;&emsp;answer|||false|string||
|&emsp;&emsp;content|||false|string||
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;judgeCases|||false|array|JudgeCase|
|&emsp;&emsp;&emsp;&emsp;input|||false|string||
|&emsp;&emsp;&emsp;&emsp;output|||false|string||
|&emsp;&emsp;judgeConfig|||false|JudgeConfig|JudgeConfig|
|&emsp;&emsp;&emsp;&emsp;memoryLimit|||false|integer||
|&emsp;&emsp;&emsp;&emsp;stackLimit|||false|integer||
|&emsp;&emsp;&emsp;&emsp;timeLimit|||false|integer||
|&emsp;&emsp;tags|||false|array|string|
|&emsp;&emsp;title|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«boolean»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||boolean||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": true,
	"message": ""
}
```


## 获取题目信息


**接口地址**:`/api/question/get`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|id|query|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«Question»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||Question|Question|
|&emsp;&emsp;acNum||integer(int32)||
|&emsp;&emsp;answer||string||
|&emsp;&emsp;content||string||
|&emsp;&emsp;createTime||string(date)||
|&emsp;&emsp;favourNum||integer(int32)||
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;isDelete||integer(int32)||
|&emsp;&emsp;judgeCase||string||
|&emsp;&emsp;judgeConfig||string||
|&emsp;&emsp;submitNum||integer(int32)||
|&emsp;&emsp;tags||string||
|&emsp;&emsp;thumbNum||integer(int32)||
|&emsp;&emsp;title||string||
|&emsp;&emsp;updateTime||string(date)||
|&emsp;&emsp;userId||integer(int64)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {
		"acNum": 0,
		"answer": "",
		"content": "",
		"createTime": "",
		"favourNum": 0,
		"id": 0,
		"isDelete": 0,
		"judgeCase": "",
		"judgeConfig": "",
		"submitNum": 0,
		"tags": "",
		"thumbNum": 0,
		"title": "",
		"updateTime": "",
		"userId": 0
	},
	"message": ""
}
```


## 获取题目VO信息


**接口地址**:`/api/question/get/vo`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|id|query|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«QuestionVO»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||QuestionVO|QuestionVO|
|&emsp;&emsp;acNum||integer(int32)||
|&emsp;&emsp;content||string||
|&emsp;&emsp;createTime||string(date)||
|&emsp;&emsp;favourNum||integer(int32)||
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;judgeConfig||JudgeConfig|JudgeConfig|
|&emsp;&emsp;&emsp;&emsp;memoryLimit||integer||
|&emsp;&emsp;&emsp;&emsp;stackLimit||integer||
|&emsp;&emsp;&emsp;&emsp;timeLimit||integer||
|&emsp;&emsp;submitNum||integer(int32)||
|&emsp;&emsp;tags||array|string|
|&emsp;&emsp;thumbNum||integer(int32)||
|&emsp;&emsp;title||string||
|&emsp;&emsp;updateTime||string(date)||
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;userVO||UserVO|UserVO|
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;nickname||string||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;userAvatar||string||
|&emsp;&emsp;&emsp;&emsp;userProfile||string||
|&emsp;&emsp;&emsp;&emsp;userRole||string||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {
		"acNum": 0,
		"content": "",
		"createTime": "",
		"favourNum": 0,
		"id": 0,
		"judgeConfig": {
			"memoryLimit": 0,
			"stackLimit": 0,
			"timeLimit": 0
		},
		"submitNum": 0,
		"tags": [],
		"thumbNum": 0,
		"title": "",
		"updateTime": "",
		"userId": 0,
		"userVO": {
			"createTime": "",
			"email": "",
			"gender": 0,
			"id": 0,
			"nickname": "",
			"phone": "",
			"userAvatar": "",
			"userProfile": "",
			"userRole": ""
		}
	},
	"message": ""
}
```


## 分页获取当前用户创建的题目列表


**接口地址**:`/api/question/list/my/page/vo`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "current": 0,
  "id": 0,
  "pageSize": 0,
  "sortField": "",
  "sortOrder": "",
  "tags": [],
  "title": "",
  "userId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionQueryRequest|questionQueryRequest|body|true|QuestionQueryRequest|QuestionQueryRequest|
|&emsp;&emsp;current|||false|integer(int64)||
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;pageSize|||false|integer(int64)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;tags|||false|array|string|
|&emsp;&emsp;title|||false|string||
|&emsp;&emsp;userId|||false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«Page«QuestionVO»»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||Page«QuestionVO»|Page«QuestionVO»|
|&emsp;&emsp;countId||string||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;optimizeCountSql||boolean||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;records||array|QuestionVO|
|&emsp;&emsp;&emsp;&emsp;acNum||integer||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;favourNum||integer||
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;judgeConfig||JudgeConfig|JudgeConfig|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;memoryLimit||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;stackLimit||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;timeLimit||integer||
|&emsp;&emsp;&emsp;&emsp;submitNum||integer||
|&emsp;&emsp;&emsp;&emsp;tags||array|string|
|&emsp;&emsp;&emsp;&emsp;thumbNum||integer||
|&emsp;&emsp;&emsp;&emsp;title||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;userVO||UserVO|UserVO|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;nickname||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userAvatar||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userProfile||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userRole||string||
|&emsp;&emsp;searchCount||boolean||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {
		"countId": "",
		"current": 0,
		"maxLimit": 0,
		"optimizeCountSql": true,
		"orders": [
			{
				"asc": true,
				"column": ""
			}
		],
		"pages": 0,
		"records": [
			{
				"acNum": 0,
				"content": "",
				"createTime": "",
				"favourNum": 0,
				"id": 0,
				"judgeConfig": {
					"memoryLimit": 0,
					"stackLimit": 0,
					"timeLimit": 0
				},
				"submitNum": 0,
				"tags": [],
				"thumbNum": 0,
				"title": "",
				"updateTime": "",
				"userId": 0,
				"userVO": {
					"createTime": "",
					"email": "",
					"gender": 0,
					"id": 0,
					"nickname": "",
					"phone": "",
					"userAvatar": "",
					"userProfile": "",
					"userRole": ""
				}
			}
		],
		"searchCount": true,
		"size": 0,
		"total": 0
	},
	"message": ""
}
```


## 管理员分页获取题目列表（所有信息）


**接口地址**:`/api/question/list/page`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "current": 0,
  "id": 0,
  "pageSize": 0,
  "sortField": "",
  "sortOrder": "",
  "tags": [],
  "title": "",
  "userId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionQueryRequest|questionQueryRequest|body|true|QuestionQueryRequest|QuestionQueryRequest|
|&emsp;&emsp;current|||false|integer(int64)||
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;pageSize|||false|integer(int64)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;tags|||false|array|string|
|&emsp;&emsp;title|||false|string||
|&emsp;&emsp;userId|||false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«Page«Question»»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||Page«Question»|Page«Question»|
|&emsp;&emsp;countId||string||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;optimizeCountSql||boolean||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;records||array|Question|
|&emsp;&emsp;&emsp;&emsp;acNum||integer||
|&emsp;&emsp;&emsp;&emsp;answer||string||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;favourNum||integer||
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;isDelete||integer||
|&emsp;&emsp;&emsp;&emsp;judgeCase||string||
|&emsp;&emsp;&emsp;&emsp;judgeConfig||string||
|&emsp;&emsp;&emsp;&emsp;submitNum||integer||
|&emsp;&emsp;&emsp;&emsp;tags||string||
|&emsp;&emsp;&emsp;&emsp;thumbNum||integer||
|&emsp;&emsp;&emsp;&emsp;title||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;searchCount||boolean||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {
		"countId": "",
		"current": 0,
		"maxLimit": 0,
		"optimizeCountSql": true,
		"orders": [
			{
				"asc": true,
				"column": ""
			}
		],
		"pages": 0,
		"records": [
			{
				"acNum": 0,
				"answer": "",
				"content": "",
				"createTime": "",
				"favourNum": 0,
				"id": 0,
				"isDelete": 0,
				"judgeCase": "",
				"judgeConfig": "",
				"submitNum": 0,
				"tags": "",
				"thumbNum": 0,
				"title": "",
				"updateTime": "",
				"userId": 0
			}
		],
		"searchCount": true,
		"size": 0,
		"total": 0
	},
	"message": ""
}
```


## 分页获取题目VO列表


**接口地址**:`/api/question/list/page/vo`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "current": 0,
  "id": 0,
  "pageSize": 0,
  "sortField": "",
  "sortOrder": "",
  "tags": [],
  "title": "",
  "userId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionQueryRequest|questionQueryRequest|body|true|QuestionQueryRequest|QuestionQueryRequest|
|&emsp;&emsp;current|||false|integer(int64)||
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;pageSize|||false|integer(int64)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;tags|||false|array|string|
|&emsp;&emsp;title|||false|string||
|&emsp;&emsp;userId|||false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«Page«QuestionVO»»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||Page«QuestionVO»|Page«QuestionVO»|
|&emsp;&emsp;countId||string||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;optimizeCountSql||boolean||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;records||array|QuestionVO|
|&emsp;&emsp;&emsp;&emsp;acNum||integer||
|&emsp;&emsp;&emsp;&emsp;content||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;favourNum||integer||
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;judgeConfig||JudgeConfig|JudgeConfig|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;memoryLimit||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;stackLimit||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;timeLimit||integer||
|&emsp;&emsp;&emsp;&emsp;submitNum||integer||
|&emsp;&emsp;&emsp;&emsp;tags||array|string|
|&emsp;&emsp;&emsp;&emsp;thumbNum||integer||
|&emsp;&emsp;&emsp;&emsp;title||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;userVO||UserVO|UserVO|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;nickname||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userAvatar||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userProfile||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userRole||string||
|&emsp;&emsp;searchCount||boolean||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {
		"countId": "",
		"current": 0,
		"maxLimit": 0,
		"optimizeCountSql": true,
		"orders": [
			{
				"asc": true,
				"column": ""
			}
		],
		"pages": 0,
		"records": [
			{
				"acNum": 0,
				"content": "",
				"createTime": "",
				"favourNum": 0,
				"id": 0,
				"judgeConfig": {
					"memoryLimit": 0,
					"stackLimit": 0,
					"timeLimit": 0
				},
				"submitNum": 0,
				"tags": [],
				"thumbNum": 0,
				"title": "",
				"updateTime": "",
				"userId": 0,
				"userVO": {
					"createTime": "",
					"email": "",
					"gender": 0,
					"id": 0,
					"nickname": "",
					"phone": "",
					"userAvatar": "",
					"userProfile": "",
					"userRole": ""
				}
			}
		],
		"searchCount": true,
		"size": 0,
		"total": 0
	},
	"message": ""
}
```


## 管理员更新题目信息


**接口地址**:`/api/question/update`


**请求方式**:`PUT`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "answer": "",
  "content": "",
  "id": 0,
  "judgeCases": [
    {
      "input": "",
      "output": ""
    }
  ],
  "judgeConfig": {
    "memoryLimit": 0,
    "stackLimit": 0,
    "timeLimit": 0
  },
  "tags": [],
  "title": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionUpdateRequest|questionUpdateRequest|body|true|QuestionUpdateRequest|QuestionUpdateRequest|
|&emsp;&emsp;answer|||false|string||
|&emsp;&emsp;content|||false|string||
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;judgeCases|||false|array|JudgeCase|
|&emsp;&emsp;&emsp;&emsp;input|||false|string||
|&emsp;&emsp;&emsp;&emsp;output|||false|string||
|&emsp;&emsp;judgeConfig|||false|JudgeConfig|JudgeConfig|
|&emsp;&emsp;&emsp;&emsp;memoryLimit|||false|integer||
|&emsp;&emsp;&emsp;&emsp;stackLimit|||false|integer||
|&emsp;&emsp;&emsp;&emsp;timeLimit|||false|integer||
|&emsp;&emsp;tags|||false|array|string|
|&emsp;&emsp;title|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«boolean»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||boolean||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": true,
	"message": ""
}
```


# question-submit-info-controller


## 分页获取题目提交信息列表


**接口地址**:`/api/question_submit_info/list/page`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "current": 0,
  "pageSize": 0,
  "questionId": 0,
  "sortField": "",
  "sortOrder": "",
  "submitLanguage": "",
  "submitState": 0,
  "userId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionSubmitQueryRequest|questionSubmitQueryRequest|body|true|QuestionSubmitQueryRequest|QuestionSubmitQueryRequest|
|&emsp;&emsp;current|||false|integer(int64)||
|&emsp;&emsp;pageSize|||false|integer(int64)||
|&emsp;&emsp;questionId|||false|integer(int64)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||
|&emsp;&emsp;submitLanguage|||false|string||
|&emsp;&emsp;submitState|||false|integer(int32)||
|&emsp;&emsp;userId|||false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«Page«QuestionSubmitInfoVO»»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||Page«QuestionSubmitInfoVO»|Page«QuestionSubmitInfoVO»|
|&emsp;&emsp;countId||string||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;optimizeCountSql||boolean||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;records||array|QuestionSubmitInfoVO|
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;judgeInfo||JudgeInfo|JudgeInfo|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;memory||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;message||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;time||integer||
|&emsp;&emsp;&emsp;&emsp;questionId||integer||
|&emsp;&emsp;&emsp;&emsp;submitCode||string||
|&emsp;&emsp;&emsp;&emsp;submitLanguage||string||
|&emsp;&emsp;&emsp;&emsp;submitState||integer||
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;userVO||UserVO|UserVO|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;nickname||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userAvatar||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userProfile||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userRole||string||
|&emsp;&emsp;searchCount||boolean||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {
		"countId": "",
		"current": 0,
		"maxLimit": 0,
		"optimizeCountSql": true,
		"orders": [
			{
				"asc": true,
				"column": ""
			}
		],
		"pages": 0,
		"records": [
			{
				"createTime": "",
				"id": 0,
				"judgeInfo": {
					"memory": 0,
					"message": "",
					"time": 0
				},
				"questionId": 0,
				"submitCode": "",
				"submitLanguage": "",
				"submitState": 0,
				"userId": 0,
				"userVO": {
					"createTime": "",
					"email": "",
					"gender": 0,
					"id": 0,
					"nickname": "",
					"phone": "",
					"userAvatar": "",
					"userProfile": "",
					"userRole": ""
				}
			}
		],
		"searchCount": true,
		"size": 0,
		"total": 0
	},
	"message": ""
}
```


## 题目提交


**接口地址**:`/api/question_submit_info/submit`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "questionId": 0,
  "submitCode": "",
  "submitLanguage": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|questionSubmitAddRequest|questionSubmitAddRequest|body|true|QuestionSubmitAddRequest|QuestionSubmitAddRequest|
|&emsp;&emsp;questionId|||false|integer(int64)||
|&emsp;&emsp;submitCode|||false|string||
|&emsp;&emsp;submitLanguage|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«long»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||integer(int64)|integer(int64)|
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": 0,
	"message": ""
}
```