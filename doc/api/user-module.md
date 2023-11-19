# Api Documentation


**简介**:Api Documentation


**HOST**:localhost:8080

**联系人**: CodeJuzi (codejuzi@163.com)


**Version**:1.0


**接口路径**:/v3/api-docs


[TOC]






# UserController


## 用户修改密码


**接口地址**:`/api/user/change_pwd`


**请求方式**:`PUT`

**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "checkPassword": "",
  "newPassword": "",
  "originPassword": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userChangePwdRequest|UserChangePwdRequest|body|true|UserChangePwdRequest|UserChangePwdRequest|
|&emsp;&emsp;checkPassword|||false|string||
|&emsp;&emsp;newPassword|||false|string||
|&emsp;&emsp;originPassword|||false|string||


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


## 管理员删除用户


**接口地址**:`/api/user/delete`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


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
|deleteRequest|DeleteRequest|body|true|DeleteRequest|DeleteRequest|
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


## 管理员根据 id 获取用户


**接口地址**:`/api/user/get`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|id|query|false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«User»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||User|User|
|&emsp;&emsp;createTime||string(date)||
|&emsp;&emsp;email||string||
|&emsp;&emsp;gender||integer(int32)||
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;isDelete||integer(int32)||
|&emsp;&emsp;nickname||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;salt||string||
|&emsp;&emsp;updateTime||string(date)||
|&emsp;&emsp;userAccount||string||
|&emsp;&emsp;userAvatar||string||
|&emsp;&emsp;userPassword||string||
|&emsp;&emsp;userProfile||string||
|&emsp;&emsp;userRole||string||
|&emsp;&emsp;userState||integer(int32)||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {
		"createTime": "",
		"email": "",
		"gender": 0,
		"id": 0,
		"isDelete": 0,
		"nickname": "",
		"phone": "",
		"salt": "",
		"updateTime": "",
		"userAccount": "",
		"userAvatar": "",
		"userPassword": "",
		"userProfile": "",
		"userRole": "",
		"userState": 0
	},
	"message": ""
}
```


## 获取当前用户信息


**接口地址**:`/api/user/get/login`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«UserVO»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||UserVO|UserVO|
|&emsp;&emsp;createTime||string(date)||
|&emsp;&emsp;email||string||
|&emsp;&emsp;gender||integer(int32)||
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;nickname||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;userAvatar||string||
|&emsp;&emsp;userProfile||string||
|&emsp;&emsp;userRole||string||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {
		"createTime": "",
		"email": "",
		"gender": 0,
		"id": 0,
		"nickname": "",
		"phone": "",
		"userAvatar": "",
		"userProfile": "",
		"userRole": ""
	},
	"message": ""
}
```


## 管理员分页获取用户列表


**接口地址**:`/api/user/list/page`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "current": 0,
  "id": 0,
  "nickname": "",
  "pageSize": 0,
  "sortField": "",
  "sortOrder": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userQueryRequest|UserQueryRequest|body|true|UserQueryRequest|UserQueryRequest|
|&emsp;&emsp;current|||false|integer(int64)||
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;nickname|||false|string||
|&emsp;&emsp;pageSize|||false|integer(int64)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«Page«User»»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||Page«User»|Page«User»|
|&emsp;&emsp;countId||string||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;optimizeCountSql||boolean||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;records||array|User|
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;isDelete||integer||
|&emsp;&emsp;&emsp;&emsp;nickname||string||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;salt||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;&emsp;&emsp;userAccount||string||
|&emsp;&emsp;&emsp;&emsp;userAvatar||string||
|&emsp;&emsp;&emsp;&emsp;userPassword||string||
|&emsp;&emsp;&emsp;&emsp;userProfile||string||
|&emsp;&emsp;&emsp;&emsp;userRole||string||
|&emsp;&emsp;&emsp;&emsp;userState||integer||
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
				"email": "",
				"gender": 0,
				"id": 0,
				"isDelete": 0,
				"nickname": "",
				"phone": "",
				"salt": "",
				"updateTime": "",
				"userAccount": "",
				"userAvatar": "",
				"userPassword": "",
				"userProfile": "",
				"userRole": "",
				"userState": 0
			}
		],
		"searchCount": true,
		"size": 0,
		"total": 0
	},
	"message": ""
}
```


## 分页获取用户列表


**接口地址**:`/api/user/list/page/vo`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "current": 0,
  "id": 0,
  "nickname": "",
  "pageSize": 0,
  "sortField": "",
  "sortOrder": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userQueryRequest|UserQueryRequest|body|true|UserQueryRequest|UserQueryRequest|
|&emsp;&emsp;current|||false|integer(int64)||
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;nickname|||false|string||
|&emsp;&emsp;pageSize|||false|integer(int64)||
|&emsp;&emsp;sortField|||false|string||
|&emsp;&emsp;sortOrder|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«Page«UserVO»»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||Page«UserVO»|Page«UserVO»|
|&emsp;&emsp;countId||string||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;optimizeCountSql||boolean||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;records||array|UserVO|
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;nickname||string||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;userAvatar||string||
|&emsp;&emsp;&emsp;&emsp;userProfile||string||
|&emsp;&emsp;&emsp;&emsp;userRole||string||
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
				"email": "",
				"gender": 0,
				"id": 0,
				"nickname": "",
				"phone": "",
				"userAvatar": "",
				"userProfile": "",
				"userRole": ""
			}
		],
		"searchCount": true,
		"size": 0,
		"total": 0
	},
	"message": ""
}
```


## 用户登录


**接口地址**:`/api/user/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "userAccount": "",
  "userPassword": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userLoginRequest|UserLoginRequest|body|true|UserLoginRequest|UserLoginRequest|
|&emsp;&emsp;userAccount|||false|string||
|&emsp;&emsp;userPassword|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|BaseResponse«UserVO»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|data||UserVO|UserVO|
|&emsp;&emsp;createTime||string(date)||
|&emsp;&emsp;email||string||
|&emsp;&emsp;gender||integer(int32)||
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;nickname||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;userAvatar||string||
|&emsp;&emsp;userProfile||string||
|&emsp;&emsp;userRole||string||
|message||string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {
		"createTime": "",
		"email": "",
		"gender": 0,
		"id": 0,
		"nickname": "",
		"phone": "",
		"userAvatar": "",
		"userProfile": "",
		"userRole": ""
	},
	"message": ""
}
```


## 退出登录


**接口地址**:`/api/user/logout`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


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


## 用户注册


**接口地址**:`/api/user/register`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "checkPassword": "",
  "userAccount": "",
  "userPassword": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userRegisterRequest|UserRegisterRequest|body|true|UserRegisterRequest|UserRegisterRequest|
|&emsp;&emsp;checkPassword|||false|string||
|&emsp;&emsp;userAccount|||false|string||
|&emsp;&emsp;userPassword|||false|string||


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


## 管理员重置用户密码


**接口地址**:`/api/user/reset_pwd`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "checkPassword": "",
  "newPassword": "",
  "userId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userResetPwdRequest|UserResetPwdRequest|body|true|UserResetPwdRequest|UserResetPwdRequest|
|&emsp;&emsp;checkPassword|||false|string||
|&emsp;&emsp;newPassword|||false|string||
|&emsp;&emsp;userId|||false|integer(int64)||


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


## 管理员修改用户信息


**接口地址**:`/api/user/update`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "email": "",
  "gender": 0,
  "id": 0,
  "nickname": "",
  "phone": "",
  "userAvatar": "",
  "userProfile": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userUpdateRequest|UserUpdateRequest|body|true|UserUpdateRequest|UserUpdateRequest|
|&emsp;&emsp;email|||false|string||
|&emsp;&emsp;gender|||false|integer(int32)||
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;nickname|||false|string||
|&emsp;&emsp;phone|||false|string||
|&emsp;&emsp;userAvatar|||false|string||
|&emsp;&emsp;userProfile|||false|string||


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


## 用户修改个人用户信息


**接口地址**:`/api/user/update/my`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "email": "",
  "gender": 0,
  "id": 0,
  "nickname": "",
  "phone": "",
  "userAvatar": "",
  "userProfile": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userUpdateRequest|UserUpdateRequest|body|true|UserUpdateRequest|UserUpdateRequest|
|&emsp;&emsp;email|||false|string||
|&emsp;&emsp;gender|||false|integer(int32)||
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;nickname|||false|string||
|&emsp;&emsp;phone|||false|string||
|&emsp;&emsp;userAvatar|||false|string||
|&emsp;&emsp;userProfile|||false|string||


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


## 管理员修改用户状态信息


**接口地址**:`/api/user/update/state`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "userId": 0,
  "userState": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userStateUpdateRequest|UserStateUpdateRequest|body|true|UserStateUpdateRequest|UserStateUpdateRequest|
|&emsp;&emsp;userId|||false|integer(int64)||
|&emsp;&emsp;userState|||false|integer(int32)||


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
