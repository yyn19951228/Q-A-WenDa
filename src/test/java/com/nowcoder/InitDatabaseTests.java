package com.nowcoder;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
    UserDAO userDAO;

    @Autowired
    QuestionDAO questionDAO;

    @Test
    public void initDatabase() {
        Random random = new Random();
        for (int i = 0; i < 11; i++) {
            User user = new User();
            user.setName(String.format("USER %d", i));
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setId(i);
            user.setPassword(String.format("PASSWD %d", i));
            user.setSalt(String.format("SALT %d", i));

            userDAO.addUser(user);

            user.setPassword(String.format("New PASSWD %d", i));
            userDAO.updatePasswd(user);

            Question question = new Question();
            question.setCommentCount(i);
            question.setContent(String.format("balablablablablabal content %d", i));
            Date date = new Date();
            date.setTime(date.getTime() + 1000*3600*i);
            question.setCreatedDate(date);
            question.setTitle(String.format("title %d", i));
            question.setId(i);
            question.setUserId(i + 1);

            questionDAO.addQuestion(question);

        }
    }
}
