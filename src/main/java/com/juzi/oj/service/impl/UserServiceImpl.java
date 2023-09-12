package com.juzi.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.constants.CommonConstant;
import com.juzi.oj.constants.UserConstant;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.mapper.UserMapper;
import com.juzi.oj.model.dto.UserQueryRequest;
import com.juzi.oj.model.entity.User;
import com.juzi.oj.model.enums.UserRoleEnum;
import com.juzi.oj.service.UserService;
import com.juzi.oj.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import com.juzi.oj.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.juzi.oj.constants.UserConstant.*;

/**
 * @author codejuzi
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < MIN_ACC_LEN) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userAccount.length() > MAX_ACC_LEN) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户账号过长");
        }
        if (userPassword.length() < MIN_PWD_LEN || checkPassword.length() < MIN_PWD_LEN) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        Matcher matcher = Pattern.compile(ACC_PATTEN).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号含有特殊字符");
        }

        synchronized (userAccount.intern()) {
            // 账户不能重复
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "账号重复");
            }

            String salt = RandomStringUtils.randomAlphabetic(SALT_LEN);

            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setSalt(salt);
            user.setNickname("OJ_" + RandomStringUtils.randomAlphabetic(DEFAULT_NICK_SUFFIX_LEN));
            user.setUserAvatar(UserConstant.USER_DEFAULT_AVATAR_URL);
            user.setUserProfile("该用户很懒，什么都没有写");
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }

            return user.getId();
        }
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < MIN_ACC_LEN || userAccount.length() > MAX_ACC_LEN) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < MIN_PWD_LEN) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码错误");
        }

        // 查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User user = this.getOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR, "用户不存在");
        }
        String salt = user.getSalt();
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());

        if (!user.getUserPassword().equals(encryptPassword)) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码错误");
        }

        // 3. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return this.getUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // todo (CodeJuzi) 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    @Override
    public Boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public Boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return Boolean.TRUE;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        // TODO: 2023/9/12 替换BeanUtils，性能太差
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {

        if (userQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String nickname = userQueryRequest.getNickname();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(nickname), "nickname", nickname);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}
