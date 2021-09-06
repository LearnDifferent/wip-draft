package com.github.learndifferent.mtm.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * 反转一些 boolean，让其符合语义
 *
 * @author zhou
 * @date 2021/09/05
 */
public class ReverseUtils {

    public static boolean listNotEmpty(List<?> list) {
        return !list.isEmpty();
    }

    public static boolean collectionNotEmpty(Collection<?> collection) {
        return !CollectionUtils.isEmpty(collection);
    }

    public static boolean compareStringNotEquals(String baseString,
                                                 String anotherString) {
        return !baseString.equals(anotherString);
    }

    public static boolean notAdmin() {
        return !StpUtil.hasRole(RoleType.ADMIN.role());
    }

    public static boolean hasNoPermissionToDelete(String userName,
                                                  WebsiteDTO web) {
        boolean hasPermissionToDelete =
                web != null && web.getUserName().equals(userName);

        return !hasPermissionToDelete;
    }
}
