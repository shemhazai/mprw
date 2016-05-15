package com.github.shemhazai.mprw.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.shemhazai.mprw.domain.HashedUser;
import com.github.shemhazai.mprw.repo.HashedUserRepository;

@Component
@Scope("singleton")
public class AuthenticationManager {

	@Autowired
	private HashedUserRepository userRepository;
	private Map<String, String> userTokens;
	private Map<String, Date> tokenExpire;
	private HashGenerator hashGenerator;

	public AuthenticationManager() {
		userTokens = new HashMap<>();
		tokenExpire = new HashMap<>();
		hashGenerator = new HashGenerator();
	}

	@Scheduled(fixedDelay = 900000)
	public void deleteTokens() {
		List<String> tokens = new ArrayList<>();
		Date now = new Date();

		for (Map.Entry<String, Date> expire : tokenExpire.entrySet()) {
			if (now.after(expire.getValue()))
				tokens.add(expire.getKey());
		}

		tokens.forEach((token) -> {
			for (Map.Entry<String, String> user : userTokens.entrySet()) {
				if (token.equalsIgnoreCase(user.getValue())) {
					userTokens.remove(user.getKey());
					break;
				}
			}
			tokenExpire.remove(token);
		});
	}

	public synchronized String createToken(String email, String password) throws AuthenticationException {
		if (userRepository.existsUserWithEmail(email)) {
			HashedUser user = userRepository.selectUserByEmail(email);
			String passwordHash = hashGenerator.hash(password);

			if (passwordHash.equalsIgnoreCase(user.getPassword())) {
				StringBuilder builder = new StringBuilder();
				builder.append(user.getEmail());
				builder.append(user.getPassword());
				builder.append(new Date());

				String token = hashGenerator.hash(builder.toString());
				userTokens.put(user.getEmail(), token);
				tokenExpire.put(token, dateAfterOneHour());

				return token;
			}
		}

		throw new AuthenticationException("User not found!");
	}

	public boolean isTokenActive(String token) {
		boolean result = userTokens.containsValue(token);

		if (result)
			tokenExpire.put(token, dateAfterOneHour());

		return result;
	}

	public boolean isTokenActiveByEmail(String token, String email) {
		String savedToken = userTokens.get(email);
		if (savedToken == null)
			return false;

		boolean result = savedToken.equalsIgnoreCase(token);

		if (result)
			tokenExpire.put(token, dateAfterOneHour());

		return result;
	}

	private Date dateAfterOneHour() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		return calendar.getTime();
	}

	public HashedUserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(HashedUserRepository userRepository) {
		this.userRepository = userRepository;
	}

}
