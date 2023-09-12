package com.juzi.oj;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author codejuzi
 */
public class Main {
    public static void main(String[] args) {
        String salt = RandomStringUtils.randomAlphabetic(5);
        System.out.println("salt = " + salt);
    }
}
