package com.github.shemhazai.mprw.service.impl;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.shemhazai.mprw.domain.DbUser;
import com.github.shemhazai.mprw.domain.Token;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.domain.UserUpdateRequest;
import com.github.shemhazai.mprw.notify.MailNotifier;
import com.github.shemhazai.mprw.repo.DbUserRepository;
import com.github.shemhazai.mprw.service.UserService;
import com.github.shemhazai.mprw.utils.AuthenticationManager;
import com.github.shemhazai.mprw.utils.HashGenerator;
import com.github.shemhazai.mprw.utils.UserValidator;
import com.github.shemhazai.mprw.utils.VerificationManager;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private DbUserRepository userRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private VerificationManager verificationManager;
	@Autowired
	private MailNotifier mailNotifier;

	@Override
	public User selectUserByToken(Token token) {
		if (token.getToken() == null || token.getEmail() == null)
			return null;

		if (authenticationManager.isTokenActive(token.getToken()))
			return userRepository.selectUserByEmail(token.getEmail()).toUser();
		return null;
	}

	@Override
	public String createToken(User user) {
		if (user.getEmail() == null || user.getPassword() == null)
			return "UNAUTHORIZED";

		try {
			return authenticationManager.createToken(user.getEmail(), user.getPassword());
		} catch (AuthenticationException e) {
			return "UNAUTHORIZED";
		}
	}

	@Override
	public String createUser(User user) {
		UserValidator validator = new UserValidator();
		if (!validator.validate(user) || userRepository.existsUserWithEmail(user.getEmail()))
			return "FALSE";

		DbUser dbUser = userRepository.createUser(user.getEmail()).fromUser(user);
		userRepository.updateUser(dbUser.getId(), dbUser);

		return "TRUE";
	}

	@Override
	public String createVerifyLink(Token token, String scheme, String serverName, String serverPort) {
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

		String finalVerifyString = String.format("%s://%s:%s/mprw/rest/user/verify/%s", scheme, serverName, serverPort,
				verifyString);

		msgBuilder.append(finalVerifyString);
		msgBuilder.append("\n\n");
		mailNotifier.notifyOne(token.getEmail(), "Weryfikacja konta w MPRW.", msgBuilder.toString());
		return "TRUE";
	}

	@Override
	public String verify(String email, String hash) {
		if (verificationManager.verify(email, hash))
			return "Konto zostalo aktywowane!";
		return "Blad! Konto nie istnieje.";
	}

	@Override
	public String updateUser(UserUpdateRequest request) {
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

	@Override
	public String isVerified(String email) {
		email = email.trim();
		if (!userRepository.existsUserWithEmail(email))
			return "FALSE";

		DbUser user = userRepository.selectUserByEmail(email);
		return user.isVerified() ? "TRUE" : "FALSE";
	}

	@Override
	public String isTokenActive(String tokenHash) {
		boolean isActive = authenticationManager.isTokenActive(tokenHash);
		return isActive ? "TRUE" : "FALSE";
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
