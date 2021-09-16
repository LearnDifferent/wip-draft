package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.manager.SendEmailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 发送邀请码邮件邮件
 *
 * @author zhou
 */
@Controller
@RequestMapping("/invitation")
public class InvitationCodeController {

    private SendEmailManager sendEmailManager;

    @Autowired
    public void setSendEmailManager(SendEmailManager sendEmailManager) {
        this.sendEmailManager = sendEmailManager;
    }

    @GetMapping
    public void send(@RequestParam("email") String email) {

        sendEmailManager.sendEmail(email, "Your Invitation Code"
                , "Invitation Code：<p style=\"color: red\">qm29feh451uxn1215</p>");
    }
}
