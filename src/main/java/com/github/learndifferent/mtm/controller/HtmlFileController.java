package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.manager.HtmlFileManager;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class HtmlFileController {

    private final HtmlFileManager htmlFileManager;

    @Autowired
    public HtmlFileController(HtmlFileManager htmlFileManager) {
        this.htmlFileManager = htmlFileManager;
    }

    /**
     * 以 HTML 格式，导出当前用户收藏的所有的网页的数据
     *
     * @param response response
     */
    @GetMapping
    public void export(HttpServletResponse response) {
        String currentUsername = getCurrentUser();
        htmlFileManager.exportWebsDataByUserToHtmlFile(currentUsername, response);
    }

    @PostMapping
    public ResultVO<String> importFile(@RequestBody MultipartFile htmlFile) {
        String currentUser = getCurrentUser();
        try {
            String result = htmlFileManager.importWebsDataFromHtmlFile(htmlFile, currentUser);
            return ResultCreator.okResult(result);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultCreator.failResult();
        }
    }

    private String getCurrentUser() {
        return (String) StpUtil.getLoginId();
    }
}
