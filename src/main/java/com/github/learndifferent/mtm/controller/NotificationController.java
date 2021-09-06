package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.manager.NoticeManager;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 发送和接受消息
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/notify")
public class NotificationController {

    private final NoticeManager noticeManager;

    @Autowired
    public NotificationController(NoticeManager noticeManager) {
        this.noticeManager = noticeManager;
    }

    @SystemLog(title = "Notification", optsType = OptsType.READ)
    @GetMapping
    public ResultVO<String> getNotifications() {
        return ResultCreator.okResult(noticeManager.getNotificationsHtml());
    }

    @DeleteMapping
    public ResultVO<Boolean> delNotifications() {
        Boolean delOrAlreadyDeleted = noticeManager.ifDelNotifyOrAlreadyDeleted();
        return ResultCreator.okResult(delOrAlreadyDeleted);
    }

    @SystemLog(title = "Notification", optsType = OptsType.CREATE)
    @GetMapping("/{content}")
    public ResultVO<?> sendNotice(@PathVariable String content) {
        noticeManager.sendNotification(content);
        return ResultCreator.okResult();
    }
}
