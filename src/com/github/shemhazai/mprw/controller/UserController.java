package com.github.shemhazai.mprw.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.shemhazai.mprw.domain.DbUser;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.notify.MailNotifier;
import com.github.shemhazai.mprw.repo.DbUserRepository;
import com.github.shemhazai.mprw.utils.AuthenticationManager;
import com.github.shemhazai.mprw.utils.UserValidator;
import com.github.shemhazai.mprw.utils.VerificationManager;

@RestController
@RequestMapping("/rest/user")
public class UserController {

	@Autowired
	private DbUserRepository userRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private MailNotifier mailNotifier;
	@Autowired
	private VerificationManager verificationManager;

	@RequestMapping(value = "/createToken", method = RequestMethod.POST)
	public String createToken(@RequestBody String[] pass) {
		try {
			return authenticationManager.createToken(pass[0], pass[1]);
		} catch (AuthenticationException e) {
			return "UNAUTHORIZED";
		}
	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public boolean createUser(@RequestBody User user) {
		UserValidator validator = new UserValidator();
		if (!validator.validate(user) || userRepository.existsUserWithEmail(user.getEmail()))
			return false;

		DbUser dbUser = userRepository.createUser(user.getEmail()).fromUser(user);
		userRepository.updateUser(dbUser.getId(), dbUser);

		return true;
	}

	@RequestMapping(value = "/createVerifyLink", method = RequestMethod.POST)
	public boolean createVerifyLink(HttpServletRequest request, @RequestBody String[] tokenAndEmail) {
		if (tokenAndEmail.length != 2 || !userRepository.existsUserWithEmail(tokenAndEmail[1])
				|| !authenticationManager.isTokenActiveByEmail(tokenAndEmail[0], tokenAndEmail[1]))
			return false;

		DbUser user = userRepository.selectUserByEmail(tokenAndEmail[1]);
		String verifyString = verificationManager.createVerifyString(user);

		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append("Witaj, utworzono nowe konto w MPRW.\n");
		msgBuilder.append("Aby je aktywować, wejdź pod adres:\n\n");

		String finalVerifyString = String.format("%s://%s:%s/mprw/rest/user/verify/%s", request.getScheme(),
				request.getServerName(), request.getServerPort() + "", verifyString);

		msgBuilder.append(finalVerifyString);
		msgBuilder.append("\n\n");
		mailNotifier.notifyOne(tokenAndEmail[1], "Weryfikacja konta w MPRW.", msgBuilder.toString());
		return true;
	}

	@RequestMapping(value = "/verify/{verifyString}", method = RequestMethod.GET)
	public String verify(@PathVariable String verifyString) {
		if (verificationManager.verify(verifyString)) {
			return "Konto zostało zweryfikowane!";
		} else {
			return "Blad! Konto nie istnieje.";
		}
	}

	@RequestMapping(value = "/selectUserByEmail", method = RequestMethod.POST)
	public User selectUserByEmail(@RequestBody String[] tokenAndEmail) {
		if (tokenAndEmail.length == 2 && authenticationManager.isTokenActive(tokenAndEmail[0]))
			return userRepository.selectUserByEmail(tokenAndEmail[1]).toUser();
		return null;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public boolean updateUser(@RequestBody Object[] tokenAndUser) {
		if (tokenAndUser.length != 2)
			return false;

		try {
			String token = (String) tokenAndUser[0];
			Map<String, String> map = (LinkedHashMap<String, String>) tokenAndUser[1];

			User user = new User();
			user.setEmail(map.get("email"));
			user.setFirstName(map.get("firstName"));
			user.setLastName(map.get("lastName"));
			user.setMailAlert(Boolean.parseBoolean(map.get("mailAlert")));
			user.setPassword("");
			user.setPhone(map.get("phone"));
			user.setPhoneAlert(Boolean.parseBoolean(map.get("phoneAlert")));

			UserValidator validator = new UserValidator();
			if (!validator.validateUpdate(user) || !authenticationManager.isTokenActiveByEmail(token, user.getEmail()))
				return false;

			DbUser dbUser = userRepository.selectUserByEmail(user.getEmail()).fromUser(user);
			userRepository.updateUser(dbUser.getId(), dbUser);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@RequestMapping(value = "/isVerified", method = RequestMethod.POST)
	public boolean isVerified(@RequestBody String email) {
		email = email.trim();
		if (!userRepository.existsUserWithEmail(email))
			return false;
		return userRepository.selectUserByEmail(email).isVerified();
	}

	@RequestMapping(value = "/isTokenActive", method = RequestMethod.POST)
	public boolean isTokenActive(@RequestBody String tokenHash) {
		return authenticationManager.isTokenActive(tokenHash);
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

	public MailNotifier getMailNotifier() {
		return mailNotifier;
	}

	public void setMailNotifier(MailNotifier mailNotifier) {
		this.mailNotifier = mailNotifier;
	}

	public VerificationManager getVerificationManager() {
		return verificationManager;
	}

	public void setVerificationManager(VerificationManager verificationManager) {
		this.verificationManager = verificationManager;
	}

}
