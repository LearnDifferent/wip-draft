package com.github.learndifferent.mtm.utils;

/**
 * 第一次存入的时候，需要把结尾的 / 符号去掉
 *
 * @author zhou
 */
public class CleanUrlUtil {
    public static String cleanup(String url) {
        // 存入的时候，结尾有「/」就去掉
        String suffix = "/";
        if (url.endsWith(suffix)) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
