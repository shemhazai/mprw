package com.github.shemhazai.mprw.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.shemhazai.mprw.domain.DbUser;
import com.github.shemhazai.mprw.repo.DbUserRepository;

@Component
@Scope("singleton")
public class AuthenticationManager {

	@Autowired
	private DbUserRepository userRepository;
	private List<Token> tokens;

	public AuthenticationManager() {
		tokens = new ArrayList<>();
	}

	@Scheduled(fixedDelay = 900000)
	public void deleteTokens() {
		List<Token> tokensExpired = new ArrayList<>();
		Date now = new Date();

		tokens.forEach((token) -> {
			if (now.after(token.getExpireDate()))
				tokensExpired.add(token);
		});
		tokens.removeAll(tokensExpired);
	}

	public synchronized String createToken(String email, String password) throws AuthenticationException {
		if (userRepository.existsUserWithEmail(email)) {
			DbUser dbUser = userRepository.selectUserByEmail(email);

			HashGenerator hasher = new HashGenerator();
			String passwordHash = hasher.hash(password);

			if (passwordHash.equalsIgnoreCase(dbUser.getPassword())) {
				removeOldToken(email);
				StringBuilder builder = new StringBuilder();
				builder.append(dbUser.getId());
				builder.append(dbUser.getEmail());
				builder.append(dbUser.getPassword());
				builder.append(new Date());

				Token token = new Token();
				token.setToken(hasher.hash(builder.toString()));
				token.setEmail(dbUser.getEmail());
				token.setExpireDate(dateAfterOneHour());
				tokens.add(token);

				return token.getToken();
			}
		}

		throw new AuthenticationException("User not found!");
	}

	private void removeOldToken(String email) {
		List<Token> tokensToDelete = new ArrayList<>();
		for (Token token : tokens) {
			if (email.equals(token.getEmail()))
				tokensToDelete.add(token);
		}
		tokens.removeAll(tokensToDelete);
	}

	public boolean isTokenActive(String tokenHash) {
		Token theToken = null;
		for (Token token : tokens) {
			if (tokenHash.equalsIgnoreCase(token.getToken())) {
				theToken = token;
				break;
			}
		}

		if (theToken != null)
			theToken.setExpireDate(dateAfterOneHour());

		return theToken != null;
	}

	public boolean isTokenActiveByEmail(String tokenHash, String email) {
		Token theToken = null;
		for (Token token : tokens) {
			if (email.equalsIgnoreCase(token.getEmail())) {
				theToken = token;
				break;
			}
		}

		if (theToken != null && tokenHash.equalsIgnoreCase(theToken.getToken())) {
			theToken.setExpireDate(dateAfterOneHour());
			return true;
		}

		return false;
	}

	private Date dateAfterOneHour() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		return calendar.getTime();
	}

	public DbUserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(DbUserRepository userRepository) {
		this.userRepository = userRepository;
	}

}
