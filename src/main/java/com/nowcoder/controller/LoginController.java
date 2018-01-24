package com.nowcoder.controller;

import com.nowcoder.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg/"}, method = RequestMethod.POST)
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "next", required = false) String next,
                      HttpServletResponse response) {
        try {
            Map<String, String> map = userService.register(username, password);

            if (map.containsKey("ticket")) {
                // sign in successfully
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                // set path can let this cookie be used in all the fileds
                // include in cas and webapp_p
                cookie.setPath("/");
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }
            return "redirect:/";
        } catch (Exception e) {
            logger.error("register exception" + e.getMessage());
            return "login";
        }

    }

    @RequestMapping(path = {"/login/"}, method = RequestMethod.POST)
    public String login (Model model,
                         @RequestParam("username") String username,
                         @RequestParam("password") String password,
                         @RequestParam(value = "next", required = false) String next,
                         @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                         HttpServletResponse response) {
        try {
            Map<String, String> map = userService.login(username, password);
            if (map.containsKey("msg")) {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
            if (map.containsKey("ticket")) {
                // sign in successfully
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                // set path can let this cookie be used in all the fileds
                // include in cas and webapp_p
                cookie.setPath("/");
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }
            return "login";
        } catch (Exception e) {
            logger.error("register exception" + e.getMessage());
            return "login";
        }

    }

    @RequestMapping(path = {"/reglogin"}, method = RequestMethod.GET)
    public String reg(Model model,
                      @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }

    // log out = delete the ticket in cookie
    @RequestMapping(path = {"/logout"}, method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }
}
