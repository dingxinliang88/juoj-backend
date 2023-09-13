package com.juzi.oj.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzi.oj.model.dto.UserQueryRequest;
import com.juzi.oj.model.dto.UserUpdateRequest;
import com.juzi.oj.model.entity.User;
import com.juzi.oj.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author codejuzi
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 注册用户 id
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      http request
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @param request http request
     * @return user info
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request http request
     * @return user info
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request http request
     * @return true - 是管理员
     */
    Boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user user info
     * @return true - 是管理员
     */
    Boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request http request
     * @return true - 注销成功
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的用户信息
     *
     * @param user user info
     * @return user info
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList user list
     * @return user vo list
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest user query request
     * @return query wrapper
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    Boolean updateUser(UserUpdateRequest userUpdateRequest);

    Boolean updateSelf(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    LambdaUpdateWrapper<User> getUpdateWrapper(UserUpdateRequest userUpdateRequest);
}
