package com.juzi.oj.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class UserUpdateRequest implements Serializable {
    private Long id;
    private String userAccount;
    private String userPassword;
    private String salt;
    private String nickname;
    private String userAvatar;
    private String userProfile;
    private Integer gender;
    private String phone;
    private String email;
    private Integer userState;
    private String userRole;
}
