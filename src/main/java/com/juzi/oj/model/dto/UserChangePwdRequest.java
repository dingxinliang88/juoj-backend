package com.juzi.oj.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class UserChangePwdRequest implements Serializable {

    private static final long serialVersionUID = 702316678601085802L;

    private String originPassword;

    private String newPassword;

    private String checkPassword;
}
