package com.github.shemhazai.mprw.controller;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.shemhazai.mprw.domain.DbUser;
import com.github.shemhazai.mprw.domain.Token;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.domain.UserUpdateRequest;
import com.github.shemhazai.mprw.notify.MailNotifier;
import com.github.shemhazai.mprw.repo.DbUserRepository;
import com.github.shemhazai.mprw.utils.AuthenticationManager;
import com.github.shemhazai.mprw.utils.HashGenerator;
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
	public String createToken(@RequestBody User user) {
		try {
			if (user.getEmail() == null || user.getPassword() == null)
				return "UNAUTHORIZED";
			return authenticationManager.createToken(user.getEmail(), user.getPassword());
		} catch (AuthenticationException e) {
			return "UNAUTHORIZED";
		}
	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public String createUser(@RequestBody User user) {
		UserValidator validator = new UserValidator();
		if (!validator.validate(user) || userRepository.existsUserWithEmail(user.getEmail()))
			return "FALSE";

		DbUser dbUser = userRepository.createUser(user.getEmail()).fromUser(user);
		userRepository.updateUser(dbUser.getId(), dbUser);

		return "TRUE";
	}

	@RequestMapping(value = "/createVerifyLink", method = RequestMethod.POST)
	public String createVerifyLink(HttpServletRequest request, @RequestBody Token token) {
		if (token.getToken() == null || token.getEmail() == null)
			return "FALSE";

		if (!userRepository.existsUserWithEmail(token.getEmail())
				|| !authenticationManager.isTokenActiveByEmail(token.getToken(), token.getEmail()))
			return "FALSE";

		DbUser user = userRepository.selectUserByEmail(token.getEmail());
		String verifyString = verificationManager.createVerifyString(user);

		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append("Witaj, utworzono nowe konto w MPRW.\n");
		msgBuilder.append("Aby je aktywować, wejdź pod adres:\n\n");

		String finalVerifyString = String.format("%s://%s:%s/mprw/rest/user/verify/%s", request.getScheme(),
				request.getServerName(), request.getServerPort() + "", verifyString);

		msgBuilder.append(finalVerifyString);
		msgBuilder.append("\n\n");
		mailNotifier.notifyOne(token.getEmail(), "Weryfikacja konta w MPRW.", msgBuilder.toString());
		return "TRUE";
	}

	@RequestMapping(value = "/verify/{verifyString}", method = RequestMethod.GET)
	public String verify(@PathVariable String verifyString) {
		if (verificationManager.verify(verifyString)) {
			return "Konto zostalo aktywowane!";
		} else {
			return "Blad! Konto nie istnieje.";
		}
	}

	@RequestMapping(value = "/selectUserByEmail", method = RequestMethod.POST)
	public User selectUserByEmail(@RequestBody Token token) {
		if (token.getToken() == null || token.getEmail() == null)
			return null;

		if (authenticationManager.isTokenActive(token.getToken()))
			return userRepository.selectUserByEmail(token.getEmail()).toUser();
		return null;
	}

	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public String updateUser(@RequestBody UserUpdateRequest request) {
		if (request.getLoginEmail() == null || request.getLoginPassword() == null)
			return "FALSE";

		if (!userRepository.existsUserWithEmail(request.getLoginEmail()))
			return "FALSE";

		HashGenerator hasher = new HashGenerator();
		String passwordHash = hasher.hash(request.getLoginPassword());

		DbUser dbUser = userRepository.selectUserByEmail(request.getLoginEmail());
		if (!passwordHash.equalsIgnoreCase(dbUser.getPassword()))
			return "FALSE";

		int fieldsToUpdate = request.countFieldsToUpdate();
		int validatedFields = request.countValidatedFields();

		if (validatedFields != fieldsToUpdate)
			return "FALSE";

		request.updateUserFromRepository(userRepository);

		return "TRUE";
	}

	@RequestMapping(value = "/isVerified", method = RequestMethod.POST)
	public String isVerified(@RequestBody String email) {
		email = email.trim();
		if (!userRepository.existsUserWithEmail(email))
			return "FALSE";

		DbUser user = userRepository.selectUserByEmail(email);
		return user.isVerified() ? "TRUE" : "FALSE";
	}

	@RequestMapping(value = "/isTokenActive", method = RequestMethod.POST)
	public String isTokenActive(@RequestBody String tokenHash) {
		boolean isActive = authenticationManager.isTokenActive(tokenHash);
		return isActive ? "TRUE" : "FALSE";
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
