package com.nowcoder.service;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addQuestion(Question question) {
        // need add filter of sensitive words
        // .htmlEscape means dont translate from "<" to "&lt", html filter
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));

        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public Question selectById(int id) {
        return questionDAO.selectById(id);
    }

    public List<Question> getLatestQuestions(int userID, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userID, offset, limit);
    }
}
