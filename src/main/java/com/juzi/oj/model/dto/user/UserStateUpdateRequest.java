package com.juzi.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员修改用户状态
 *
 * @author codejuzi
 */
@Data
public class UserStateUpdateRequest implements Serializable {

    private static final long serialVersionUID = -6386012928539992927L;

    private Long userId;

    private Integer userState;
}
