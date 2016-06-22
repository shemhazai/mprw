package com.github.shemhazai.mprw.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.shemhazai.mprw.domain.Token;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.domain.UserUpdateRequest;
import com.github.shemhazai.mprw.service.UserService;

@RestController
@RequestMapping("/rest/user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/createToken", method = RequestMethod.POST)
	public String createToken(@RequestBody User user) {
		return userService.createToken(user);
	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public String createUser(@RequestBody User user) {
		return userService.createUser(user);
	}

	@RequestMapping(value = "/createVerifyLink", method = RequestMethod.POST)
	public String createVerifyLink(HttpServletRequest request, @RequestBody Token token) {
		return userService.createVerifyLink(token, request.getScheme(), request.getServerName(),
				request.getServerPort() + "");
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
		return userService.isVerified(email);
	}

	@RequestMapping(value = "/isTokenActive", method = RequestMethod.POST)
	public String isTokenActive(@RequestBody String tokenHash) {
		return userService.isTokenActive(tokenHash);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
