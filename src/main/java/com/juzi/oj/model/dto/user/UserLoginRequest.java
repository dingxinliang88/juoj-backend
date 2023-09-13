package com.juzi.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class UserLoginRequest implements Serializable {

    private String userAccount;

    private String userPassword;
}
