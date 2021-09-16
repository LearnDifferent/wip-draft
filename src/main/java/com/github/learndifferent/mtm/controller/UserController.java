package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.SystemLog;
import com.github.learndifferent.mtm.annotation.validation.delete.user.DeleteUserPermissionValidation;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.vo.UserBasicInfoVO;
import com.github.learndifferent.mtm.vo.UserChangePwdVO;
import com.github.learndifferent.mtm.annotation.validation.register.RegisterCodeCheck;
import com.github.learndifferent.mtm.annotation.validation.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.consist.CodeConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用于注册、更新和删除用户
 *
 * @author zhou
 */

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @SystemLog(title = "user", optsType = OptsType.OTHER)
    @GetMapping
    public ResultVO<List<UserDTO>> getUsers() {
        return ResultCreator.okResult(userService.getUsers());
    }

    @NotGuest
    @PostMapping("/changePwd")
    public ResultVO<?> changePassword(@RequestBody UserChangePwdVO user) {

        boolean success = userService.changePassword(user);

        return success ? ResultCreator.okResult()
                : ResultCreator.result(ResultCode.UPDATE_FAILED);
    }

    @RegisterCodeCheck(codeParamName = CodeConstant.CODE,
            verifyTokenParamName = CodeConstant.VERIFY_TOKEN,
            roleParamName = "role",
            invitationCodeParamName = CodeConstant.INVITATION_CODE)
    @PostMapping("/create")
    public ResultVO<?> createUser(@RequestBody UserBasicInfoVO basicInfo) {
        boolean success = userService.addUserByBasicInfo(basicInfo);

        return success ? ResultCreator.okResult()
                : ResultCreator.defaultFailResult();
    }

    @DeleteUserPermissionValidation(usernameParamName = "userName")
    @DeleteMapping("/delete")
    public ResultVO<?> deleteUser(@RequestParam("userName") String userName) {

        return userService.delUserByName(userName) ? ResultCreator.okResult()
                : ResultCreator.result(ResultCode.DELETE_FAILED);
    }
}
