package com.github.shemhazai.mprw.controller;

import java.util.Map;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.shemhazai.mprw.domain.DbUser;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.repo.DbUserRepository;
import com.github.shemhazai.mprw.utils.AuthenticationManager;
import com.github.shemhazai.mprw.utils.HashGenerator;
import com.github.shemhazai.mprw.utils.UserValidator;

@RestController
@RequestMapping("/rest/user")
public class UserController {

	@Autowired
	private DbUserRepository userRepository;
	@Autowired
	private AuthenticationManager authenticationManager;

	@RequestMapping(value = "/createToken", method = RequestMethod.POST)
	public String createToken(@RequestBody String[] pass) {
		try {
			return authenticationManager.createToken(pass[0], pass[1]);
		} catch (AuthenticationException e) {
			return "Unauthorized";
		}
	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public boolean createUser(@RequestBody User user) {
		UserValidator validator = new UserValidator();
		if (!validator.validate(user) || userRepository.existsUserWithEmail(user.getEmail()))
			return false;

		HashGenerator hasher = new HashGenerator();

		DbUser dbUser = userRepository.createUser(user.getEmail());
		dbUser.setFirstName(user.getFirstName());
		dbUser.setLastName(user.getLastName());
		dbUser.setPassword(hasher.hash(user.getPassword()));
		dbUser.setPhone(user.getPhone());
		dbUser.setMailAlert(user.isMailAlert());
		dbUser.setPhoneAlert(user.isPhoneAlert());

		userRepository.updateUser(dbUser.getId(), dbUser);

		return true;
	}

	@RequestMapping(value = "/selectUserByEmail", method = RequestMethod.POST)
	public User selectUserById(@RequestBody String[] tokenAndEmail) {
		if (tokenAndEmail.length == 2 && authenticationManager.isTokenActive(tokenAndEmail[0]))
			return userRepository.selectUserByEmail(tokenAndEmail[1]).toUser();
		return null;
	}

	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public boolean updateUser(@RequestBody Object[] tokenAndUser) {
		if (tokenAndUser.length != 2)
			return false;

		try {
			String token = (String) tokenAndUser[0];

			@SuppressWarnings("rawtypes")
			Map map = (Map) tokenAndUser[1];

			User user = new User();
			user.setEmail((String) map.get("email"));
			user.setFirstName((String) map.get("firstName"));
			user.setLastName((String) map.get("lastName"));
			user.setPhone((String) map.get("phone"));
			user.setPassword((String) map.get("password"));
			user.setMailAlert((Boolean) map.get("mailAlert"));
			user.setPhoneAlert((Boolean) map.get("phoneAlert"));

			UserValidator validator = new UserValidator();
			if (!validator.validate(user) || !authenticationManager.isTokenActiveByEmail(token, user.getEmail()))
				return false;

			HashGenerator hasher = new HashGenerator();

			DbUser dbUser = userRepository.selectUserByEmail(user.getEmail());
			dbUser.setFirstName(user.getFirstName());
			dbUser.setLastName(user.getLastName());
			dbUser.setPassword(hasher.hash(user.getPassword()));
			dbUser.setPhone(user.getPhone());
			dbUser.setMailAlert(user.isMailAlert());
			dbUser.setPhoneAlert(user.isPhoneAlert());

			userRepository.updateUser(dbUser.getId(), dbUser);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public DbUserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(DbUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

}
