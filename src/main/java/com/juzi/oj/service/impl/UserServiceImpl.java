package com.juzi.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.oj.common.StatusCode;
import com.juzi.oj.constants.CommonConstant;
import com.juzi.oj.constants.UserConstant;
import com.juzi.oj.exception.BusinessException;
import com.juzi.oj.mapper.UserMapper;
import com.juzi.oj.model.dto.user.*;
import com.juzi.oj.model.entity.User;
import com.juzi.oj.model.enums.UserRoleEnum;
import com.juzi.oj.model.vo.UserVO;
import com.juzi.oj.service.UserService;
import com.juzi.oj.utils.SqlUtils;
import com.juzi.oj.utils.SysUtils;
import com.juzi.oj.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.juzi.oj.constants.CommonConstant.MAX_FETCH_SIZE;
import static com.juzi.oj.constants.UserConstant.*;

/**
 * @author codejuzi
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final Pattern ACC_MATCHER = Pattern.compile(ACC_PATTEN);

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
        if (userPassword.length() != checkPassword.length() || !userPassword.equals(checkPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        if (!ACC_MATCHER.matcher(userAccount).find()) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号含有特殊字符");
        }

        synchronized (userAccount.intern()) {
            // 账户不能重复
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            long count = this.count(queryWrapper);
            ThrowUtils.throwIf(count > 0, new BusinessException(StatusCode.PARAMS_ERROR, "账号重复"));

            // 生成盐值
            String salt = SysUtils.genSalt();

            // 2. 加密
            String encryptPassword = SysUtils.encryptPassword(salt, userPassword);
            // 3. 插入数据
            User user = User.builder()
                    .userAccount(userAccount)
                    .userPassword(encryptPassword)
                    .salt(salt)
                    .nickname(SysUtils.genDefaultNickname())
                    .userAvatar(DEFAULT_USER_AVATAR)
                    .userProfile(DEFAULT_USER_PROFILE)
                    .build();
            boolean saveResult = this.save(user);

            ThrowUtils.throwIf(!saveResult, new BusinessException(StatusCode.SYSTEM_ERROR, "注册失败，数据库错误"));

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

        // 2. 加密
        String salt = user.getSalt();
        String encryptPassword = SysUtils.encryptPassword(salt, userPassword);

        if (!user.getUserPassword().equals(encryptPassword)) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码错误");
        }

        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(StatusCode.NOT_LOGIN_ERROR, "未登录");
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
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return Boolean.TRUE;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
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

    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
        Long current = userQueryRequest.getCurrent();
        Long size = userQueryRequest.getPageSize();
        // 限制爬虫
        if (size > MAX_FETCH_SIZE) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "一次性获取数据过多！");
        }
        Page<User> userPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = this.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return userVOPage;
    }

    @Override
    public Boolean updateUser(UserUpdateRequest userUpdateRequest) {
        Long userId = userUpdateRequest.getId();
        if (userId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        long count = this.count(new LambdaQueryWrapper<User>().eq(User::getId, userId));
        if (count == 0) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        LambdaUpdateWrapper<User> updateWrapper = this.getUpdateWrapper(userUpdateRequest);
        boolean updateRes = this.update(updateWrapper);

        if (!updateRes) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateSelf(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        Long userId = userUpdateRequest.getId();
        if (userId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        long count = this.count(new LambdaQueryWrapper<User>().eq(User::getId, userId));
        if (count == 0) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }

        User loginUser = this.getLoginUser(request);

        if (!userId.equals(loginUser.getId())) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        }

        LambdaUpdateWrapper<User> updateWrapper = this.getUpdateWrapper(userUpdateRequest);
        boolean updateRes = this.update(updateWrapper);

        if (!updateRes) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR);
        }
        return Boolean.TRUE;
    }

    @Override
    public LambdaUpdateWrapper<User> getUpdateWrapper(UserUpdateRequest userUpdateRequest) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        Long id = userUpdateRequest.getId();
        String nickname = userUpdateRequest.getNickname();
        String userAvatar = userUpdateRequest.getUserAvatar();
        String userProfile = userUpdateRequest.getUserProfile();
        Integer gender = userUpdateRequest.getGender();
        String phone = userUpdateRequest.getPhone();
        String email = userUpdateRequest.getEmail();

        updateWrapper.eq(User::getId, id)
                .set(StringUtils.isNotBlank(nickname), User::getNickname, nickname)
                .set(StringUtils.isNotBlank(userAvatar), User::getUserAvatar, userAvatar)
                .set(StringUtils.isNotBlank(userProfile), User::getUserProfile, userProfile)
                .set(GENDER_SET.contains(gender), User::getGender, gender)
                .set(StringUtils.isNotBlank(phone), User::getPhone, phone)
                .set(StringUtils.isNotBlank(email), User::getEmail, email);

        return updateWrapper;
    }

    @Override
    public Boolean changePwd(UserChangePwdRequest userChangePwdRequest, HttpServletRequest request) {
        String originPassword = userChangePwdRequest.getOriginPassword();
        String newPassword = userChangePwdRequest.getNewPassword();
        String checkPassword = userChangePwdRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(originPassword, newPassword, checkPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        if (originPassword.length() < MIN_PWD_LEN || newPassword.length() < MIN_PWD_LEN || checkPassword.length() < MIN_PWD_LEN) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码错误");
        }

        if (newPassword.length() != checkPassword.length() || !newPassword.equals(checkPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "两次输入密码不一致");
        }

        // 判断密码是否正确
        User loginUser = this.getLoginUser(request);
        String encryptPassword = SysUtils.encryptPassword(loginUser.getSalt(), originPassword);
        if (!loginUser.getUserPassword().equals(encryptPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "原始密码错误");
        }
        // 生成新的盐值和新的密码
        String newSalt = SysUtils.genSalt();
        String newEncryptPassword = SysUtils.encryptPassword(newSalt, newPassword);

        // 更新数据库信息
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, loginUser.getId())
                .set(User::getSalt, newSalt)
                .set(User::getUserPassword, newEncryptPassword);

        boolean updateRes = this.update(updateWrapper);
        if (!updateRes) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR);
        }
        // 删除用户的Session， 重新登录
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateState(UserStateUpdateRequest userStateUpdateRequest) {
        Long userId = userStateUpdateRequest.getUserId();
        Integer userState = userStateUpdateRequest.getUserState();

        if (userId <= 0 || !STATE_SET.contains(userState)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId)
                .set(User::getUserState, userState);
        boolean updateRes = this.update(updateWrapper);
        if (!updateRes) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean resetUserPwd(UserResetPwdRequest userResetPwdRequest) {
        Long userId = userResetPwdRequest.getUserId();
        String newPassword = userResetPwdRequest.getNewPassword();
        String checkPassword = userResetPwdRequest.getCheckPassword();

        if (userId <= 0 || StringUtils.isAnyBlank(newPassword, checkPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }

        if (newPassword.length() < MIN_PWD_LEN || checkPassword.length() < MIN_PWD_LEN) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码过短");
        }

        if (newPassword.length() != checkPassword.length() || !newPassword.equals(checkPassword)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        // 生成新的盐值和新的密码
        String newSalt = SysUtils.genSalt();
        String newEncryptPassword = SysUtils.encryptPassword(newSalt, newPassword);

        // 更新数据库信息
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId)
                .set(User::getSalt, newSalt)
                .set(User::getUserPassword, newEncryptPassword);

        boolean updateRes = this.update(updateWrapper);
        if (!updateRes) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR);
        }
        return Boolean.TRUE;
    }


}
