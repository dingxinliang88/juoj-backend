package com.juzi.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class UserResetPwdRequest implements Serializable {

    private static final long serialVersionUID = 702316678601085802L;

    private Long userId;

    private String newPassword;

    private String checkPassword;
}
