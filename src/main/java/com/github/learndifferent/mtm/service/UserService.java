package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.vo.UserBasicInfoVO;
import com.github.learndifferent.mtm.vo.UserChangePwdVO;
import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.entity.UserDO;

import java.util.List;

/**
 * UserService
 *
 * @author zhou
 * @date 2021/09/05
 */
public interface UserService {

    /**
     * 获取所有用户的名称及其收藏的网页的个数，并按照网页个数排序
     *
     * @return 包装为只含有 userName 和 webCount 的 User 类列表
     */
    List<UserWithWebCountDTO> getNamesAndCountMarkedWebDesc();

    /**
     * 获取某些用户的名称及其收藏的网页的个数，并按照网页个数排序
     *
     * @param usernames 需要获取的用户名
     * @return 包装为只含有 userName 和 webCount 的 User 类列表
     */
    List<UserWithWebCountDTO> getNamesAndCountMarkedWebDesc(List<String> usernames);

    /**
     * 修改密码
     *
     * @param info 修改密码需要的信息
     * @return 修改是否成功的信息
     */
    boolean changePassword(UserChangePwdVO info);

    /**
     * 修改密码
     *
     * @param userName    用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改是否成功的信息
     */
    boolean changePassword(String userName, String oldPassword, String newPassword);

    /**
     * 添加用户，并给用户增加 ID 和 CreateTime，将密码加密
     * ，如果已经存在，会抛出用户已存在的运行时异常
     *
     * @param user 被添加的用户
     * @return 成功与否
     */
    boolean addUser(UserDTO user);

    /**
     * 传入用户名、未加密的密码和角色，生成用户
     * ，并调用添加用户的方法将用户添加到数据库
     *
     * @param userBasicInfo 用户名、未加密的密码和角色信息
     * @return 成功与否
     */
    boolean addUserByBasicInfo(UserBasicInfoVO userBasicInfo);

    /**
     * 根据用户名和密码查找用户
     *
     * @param userName 用户名
     * @param password 密码
     * @return 用户
     */
    UserDTO getUserByNameAndPwd(String userName, String password);

    /**
     * 根据用户名获取用户角色
     *
     * @param userName 用户名
     * @return 用户角色
     */
    String getRoleByName(String userName);

    /**
     * 根据用户 ID，获取用户角色
     *
     * @param userId 用户 ID
     * @return 用户角色
     */
    String getUserRoleById(String userId);

    /**
     * 根据 ID 获取用户
     *
     * @param userId 用户 ID
     * @return 用户
     */
    UserDO getUserById(String userId);

    /**
     * 根据用户名获取用户
     *
     * @param userName 用户名
     * @return 用户
     */
    UserDO getUserByName(String userName);

    /**
     * 根据用户名删除用户
     *
     * @param userName 用户名
     * @return 是否成功删除
     */
    boolean delUserByName(String userName);

    /**
     * 更新用户数据
     *
     * @param user 用户数据
     * @return 是否更新成功
     */
    boolean updateUser(UserDTO user);

    /**
     * 获取全部用户
     *
     * @return 全部用户列表
     */
    List<UserDTO> getUsers();
}
