package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;


    public Map<String, String> register(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "username can not be empty");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "password can not be empty");
            return map;
        }

        // check if the user exists
        if (userDAO.selectByName(username) != null) {
            map.put("msg", "this user exists");
            return map;
        }

        User user = new User();
        user.setName(username);
        // use uuid as salt
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(password+user.getSalt());
        userDAO.addUser(user);


        // auto sign in after sign up
        String ticket = addLoginTicket(user.getId());
        // the ticket need to send to browser by HttpResponse

        map.put("ticket", ticket);
        return map;

    }

    public Map<String, String> login(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "username can not be empty");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "password can not be empty");
            return map;
        }

        // check if the user exists
        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("msg", "this user doesnt exist");
            logger.info(user.toString());
            return map;
        }

        if(!(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "password wrong");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        // the ticket need to send to browser by HttpResponse
        map.put("ticket", ticket);


        return map;

    }

    private String addLoginTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);

        Date now = new Date();
        // expired after 100 days
        now.setTime(3600*24*100+now.getTime());
        loginTicket.setExpired(now);

        // status 0 : valid
        //        1 : invalid
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public User getUser(int id) {
        return userDAO.selectByID(id);
    }

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }

    public void logout(String ticket) {
        // status 1 = invalid ticket
        loginTicketDAO.updateStatus(ticket, 1);
    }
}
