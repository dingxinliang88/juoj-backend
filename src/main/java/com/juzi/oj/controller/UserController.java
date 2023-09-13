package com.juzi.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.oj.aspect.AuthCheck;
import com.juzi.oj.common.BaseResponse;
import com.juzi.oj.common.DeleteRequest;
import com.juzi.oj.common.ResultUtils;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.model.dto.user.*;
import com.juzi.oj.model.entity.User;
import com.juzi.oj.model.vo.UserVO;
import com.juzi.oj.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.juzi.oj.constants.UserConstant.ADMIN_ROLE;

/**
 * 用户接口
 *
 * @author Shier
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "UserController")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.userRegister(userAccount, userPassword, checkPassword));
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.userLogin(userAccount, userPassword, request));
    }

    @PostMapping("/logout")
    @ApiOperation(value = "退出登录")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return ResultUtils.success(userService.userLogout(request));
    }

    @GetMapping("/get/login")
    @ApiOperation(value = "获取当前用户信息")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getUserVO(user));
    }

    @DeleteMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "管理员删除用户")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.removeById(deleteRequest.getId()));
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "管理员根据 id 获取用户")
    public BaseResponse<User> getUserById(Long id) {
        if (id <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(user);
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "管理员分页获取用户列表")
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取用户列表")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.listUserVOByPage(userQueryRequest));
    }

    @PutMapping("/update")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "管理员修改用户信息")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.updateUser(userUpdateRequest));
    }

    @PutMapping("/update/my")
    @ApiOperation(value = "用户修改个人用户信息")
    public BaseResponse<Boolean> updateSelf(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.updateSelf(userUpdateRequest, request));
    }

    @PutMapping("/change_pwd")
    @ApiOperation(value = "用户修改密码")
    public BaseResponse<Boolean> changePwd(@RequestBody UserChangePwdRequest userChangePwdRequest, HttpServletRequest request) {
        if (userChangePwdRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.changePwd(userChangePwdRequest, request));
    }

    @PutMapping("/update/state")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "管理员修改用户状态信息")
    public BaseResponse<Boolean> updateState(@RequestBody UserStateUpdateRequest userStateUpdateRequest) {
        if (userStateUpdateRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.updateState(userStateUpdateRequest));
    }

    @PutMapping("/reset/pwd")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "管理员重置用户密码")
    public BaseResponse<Boolean> resetUserPwd(@RequestBody UserResetPwdRequest userResetPwdRequest) {
        if (userResetPwdRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        return ResultUtils.success(userService.resetUserPwd(userResetPwdRequest));
    }

}
