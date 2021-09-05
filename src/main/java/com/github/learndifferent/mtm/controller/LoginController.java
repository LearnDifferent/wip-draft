package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.validation.login.LoginCheck;
import com.github.learndifferent.mtm.constant.consist.CodeConstant;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.vo.UserNameAndPwdVO;
import org.springframework.web.bind.annotation.*;

/**
 * 用于登陆相关
 *
 * @author zhou
 */
@RestController
@RequestMapping("/log")
public class LoginController {

    /**
     * 登陆（注解 @LoginCheck 会检查登陆信息，如果出错了，会抛出异常）
     *
     * @param nameAndPwd 用户名和密码
     * @return token 信息
     */
    @LoginCheck(codeParamName = CodeConstant.CODE,
            verifyTokenParamName = CodeConstant.VERIFY_TOKEN,
            usernameParamName = "userName",
            passwordParamName = "password")
    @PostMapping("/in")
    public ResultVO<SaTokenInfo> login(@RequestBody UserNameAndPwdVO nameAndPwd) {

        // 存放登陆信息，以用户名为 ID 即可
        StpUtil.setLoginId(nameAndPwd.getUserName());
        // 最后，获取 token 信息并返回给前端
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ResultCreator.okResult(tokenInfo);
    }

    @GetMapping("/out")
    public ResultVO<?> logout() {
        StpUtil.logout();

        return ResultCreator.okResult();
    }

}
