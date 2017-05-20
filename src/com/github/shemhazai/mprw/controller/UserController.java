package com.github.shemhazai.mprw.controller;

import com.github.shemhazai.mprw.domain.Token;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.domain.UserContact;
import com.github.shemhazai.mprw.domain.UserUpdateRequest;
import com.github.shemhazai.mprw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/rest/user")
public class UserController {

    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/createToken", method = RequestMethod.POST)
    public String createToken(@RequestBody User user) {
        try {
            return userService.createToken(user).getToken();
        } catch (AuthenticationException e) {
            return UNAUTHORIZED;
        }
    }

    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public String createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @RequestMapping(value = "/sendVerifyLink", method = RequestMethod.POST)
    public String sendVerifyLink(HttpServletRequest request, @RequestBody Token token) {
        return userService.sendVerifyLink(token, request.getScheme(), request.getServerName(),
                Integer.toString(request.getServerPort()));
    }

    @RequestMapping(value = "/verify/{email}/{hash}", method = RequestMethod.GET)
    public String verify(@PathVariable String email, @PathVariable String hash) {
        return userService.verify(email, hash);
    }

    @RequestMapping(value = "/selectUserByToken", method = RequestMethod.POST)
    public User selectUserByToken(@RequestBody Token token) {
        return userService.selectUserByToken(token);
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public String updateUser(@RequestBody UserUpdateRequest request) {
        return userService.updateUser(request);
    }

    @RequestMapping(value = "/isVerified", method = RequestMethod.POST)
    public String isVerified(@RequestBody String email) {
        if (userService.isVerified(email))
            return TRUE;
        return FALSE;
    }

    @RequestMapping(value = "/isTokenRegistered", method = RequestMethod.POST)
    public String isTokenRegistered(@RequestBody Token token) {
        if (userService.isTokenRegistered(token))
            return TRUE;
        return FALSE;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public String contact(@RequestBody UserContact contact) {
        if (userService.contact(contact))
            return TRUE;
        return FALSE;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
