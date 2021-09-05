package com.github.learndifferent.mtm.annotation.validation.login;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.VerificationCodeManager;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.JsonUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 检查登陆信息是否正确。
 * 登陆信息从 request 中获取，如果从 param 中没有获取到，就从 body 中获取相应的信息。
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
public class LoginCheckAspect {

    private final VerificationCodeManager codeManager;

    private final UserService userService;

    @Autowired
    public LoginCheckAspect(VerificationCodeManager codeManager,
                            UserService userService) {
        this.codeManager = codeManager;
        this.userService = userService;
    }

    /**
     * 验证登陆相关数据，如果出错，就抛出异常
     *
     * @param loginCheck 注解
     * @throws ServiceException 验证出错，就抛出异常
     */
    @Before("@annotation(loginCheck)")
    public void check(LoginCheck loginCheck) {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new ServiceException("No attributes available.");
        }

        ContentCachingRequestWrapper request = (ContentCachingRequestWrapper) attributes.getRequest();

        String codeParamName = loginCheck.codeParamName();
        String verifyTokenParamName = loginCheck.verifyTokenParamName();
        String usernameParamName = loginCheck.usernameParamName();
        String passwordParamName = loginCheck.passwordParamName();

        // <param's name, param's value>
        Map<String, String> contents = getContents(
                request,
                codeParamName,
                verifyTokenParamName,
                usernameParamName,
                passwordParamName);

        // 如果参数的值中，有 null 的话，就从 Request 的 Body 中获取值
        boolean shouldRenewValueFromBody = contents.containsValue(null);
        if (shouldRenewValueFromBody) {
            // 从 Request Body 中获取值，如果还是为 null，就转化为空字符串
            renewContentsFromBody(request, contents);
        }

        checkBeforeLogin(contents, codeParamName, verifyTokenParamName,
                usernameParamName, passwordParamName);
    }

    private Map<String, String> getContents(ContentCachingRequestWrapper request, String... paramNames) {

        Map<String, String> contents = new HashMap<>(16);

        for (String key : paramNames) {
            String value = request.getParameter(key);
            contents.put(key, value);
        }

        return contents;
    }

    private void renewContentsFromBody(ContentCachingRequestWrapper request,
                                       Map<String, String> contents) {

        Map<String, String> requestBody = getRequestBody(request);

        // 将 request body 的内容直接覆盖进来
        contents.putAll(requestBody);

        // 如果值还是为 null，就转换为空字符串
        contents.replaceAll((k, v) -> v == null ? "" : v);
    }

    private Map<String, String> getRequestBody(ContentCachingRequestWrapper request) {

        byte[] contentAsByteArray = request.getContentAsByteArray();

        String json = new String(contentAsByteArray);

        TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {
        };

        return JsonUtils.toObject(json, typeRef);
    }

    private void checkBeforeLogin(Map<String, String> contents,
                                  String codeParamName,
                                  String verifyTokenParamName,
                                  String usernameParamName,
                                  String passwordParamName) {

        checkBeforeLogin(contents.get(codeParamName),
                contents.get(verifyTokenParamName),
                contents.get(usernameParamName),
                contents.get(passwordParamName));
    }

    private void checkBeforeLogin(String code,
                                  String verifyToken,
                                  String username,
                                  String password) {

        // 如果验证码错误，会抛出错误异常
        codeManager.checkCode(verifyToken, code);

        // 验证码正确，就查找用户
        UserDTO user = userService.getUserByNameAndPwd(username, password);

        if (ObjectUtils.isEmpty(user)) {
            // 如果用户不存在，抛出不存在的异常（也就是用户名或密码不正确）
            throw new ServiceException(ResultCode.USER_LOGIN_FAIL);
        }
    }

}
