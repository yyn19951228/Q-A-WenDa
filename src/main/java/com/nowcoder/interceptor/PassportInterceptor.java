package com.nowcoder.interceptor;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

// check if the user is valid
// user identification check
@Component
public class PassportInterceptor implements HandlerInterceptor{

    // check if the ticket is valid
    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    HostHolder hostHolder;

    // before handler
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        // example of how to get cookie
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        if (ticket != null) {
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                return true;
            }
            User user = userDAO.selectByID(loginTicket.getUserId());

            hostHolder.setUser(user);
        }

        return true;
    }

    // after handler, before render
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        // model and view(views are .html in resources)
        // set users in context in html
        if (modelAndView != null && hostHolder.getUser() != null) {
            // like model.addAttributes, so the "user" are accessible in velocity templates
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    // after all the operator
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
