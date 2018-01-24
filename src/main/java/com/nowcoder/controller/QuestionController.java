package com.nowcoder.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

// an ajax request
// return json
// return error code = 0 : valid
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;


    @RequestMapping(value = "/question/add", method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            // if no user is logging in
            if (hostHolder.getUser() == null) {
                // give an anonymous user ID
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
            } else {
                question.setUserId(hostHolder.getUser().getId());
            }
            // return code = 0 : add questions successfully
            if (questionService.addQuestion(question) > 0) {
                return WendaUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("add question failed" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "add questions failed");
    }

    @RequestMapping(value = "/question/{qid}")
    public String questionDetail(Model model,
                                 @PathVariable(value = "qid") int qid) {
        Question question = questionService.selectById(qid);
        model.addAttribute("question", question);
        model.addAttribute("user", userService.getUser(question.getUserId()));
        return "detail";
    }
}
