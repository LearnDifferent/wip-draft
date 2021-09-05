package com.github.learndifferent.mtm.utils;

import org.springframework.util.DigestUtils;

/**
 * md5 工具
 *
 * @author zhou
 * @date 2021/09/05
 */
public class Md5Util {

    private static String salt = "acDn156";

    public static String getMd5(String str) {
        String base = str + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

}