package com.nowcoder.controller;

import com.nowcoder.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

// @Controller
public class IndexController {

    // requestion map to different path
    // 127.0.0.1:8080/ or 127.0.0.1:8080/index
    // method = requestMethod.POST means only support for method ""
    @RequestMapping(path = {"/", "/index"})
    @ResponseBody
    public String index(HttpSession session) {
        if (session.getAttribute("msg") != null) {
            return "Hello Baby  " + session.getAttribute("msg");
        }
        return "Hello Baby";

    }

    // http://127.0.0.1:8080/profile/1/1
    @RequestMapping(path = {"/profile/{groupID}/{userID}"})
    @ResponseBody
    // use @PathVariable to parse the variable in path
    // use @REquestParam to parse the request parameter like http://127.0.0.1:8080/profile/1/1?key=123&type=t
    public String profile(@PathVariable("userID") int userID,
                          @PathVariable("groupID") int groupID,
                          @RequestParam(value = "key", defaultValue = "keyDValue") int key,
                          @RequestParam(value = "type", required = false) int type) {
        return String.format("userID is %d, groupID is %d", userID, groupID);
    }


    @RequestMapping(path = {"/vm"}, method = RequestMethod.GET)
    // model is for transmitting value into template
    public String template(Model model) {
        // pase value to html
        model.addAttribute("value1", "value1");
        List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});
        model.addAttribute("colors", colors);

        model.addAttribute("user", new User("Yining"));
        return "Baby";
    }

    @RequestMapping(path = {"/request"}, method = RequestMethod.GET)
    @ResponseBody
    public String requestMethod(Model model,
                                HttpServletResponse response,
                                HttpServletRequest request,
                                HttpSession httpSession,
                                // parse cookie value directly from cookie
                                @CookieValue("JSESSIONID") String sessionID) {
        StringBuilder sb = new StringBuilder();
        StringBuilder csb = new StringBuilder();
        csb.append(sessionID + "<br>");
        // the use of Enumeration
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                csb.append("Cookie Name:" + cookie.getName() + "value:" + cookie.getValue());
            }
        }

        response.addHeader("UserID", "Yining");
        response.addCookie(new Cookie("userNmae:", "Yiqun"));

        // for write picture stream
//        response.getOutputStream().write();
        return csb.toString();
    }

    // redirect
    @RequestMapping(path = {"/redirect/{code}"}, method = RequestMethod.GET)
    // attention: no @ResponseBody here
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession session) {
        session.setAttribute("msg", "jump from redirect");
        // return to http://127.0.0.1:8080/
        RedirectView red = new RedirectView("/", true);
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    @RequestMapping(path = {"/admin"}, method = RequestMethod.GET)
    @ResponseBody
    public String adminRequest(@RequestParam(value = "key", required = true) String key) {
        if ("admin".equals(key)) {
            return "welcome";
        }
        throw new IllegalArgumentException("wrong passwd");
    }

    // and error catch
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();
    }

}
