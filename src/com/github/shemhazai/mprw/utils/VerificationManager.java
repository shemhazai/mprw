package com.github.shemhazai.mprw.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.shemhazai.mprw.domain.DbUser;
import com.github.shemhazai.mprw.repo.DbUserRepository;

@Component
public class VerificationManager {

	@Autowired
	private DbUserRepository userRepository;

	public synchronized String createVerifyString(DbUser user) {
		String string = user.getId() + ":" + user.getEmail();
		HashGenerator hasher = new HashGenerator();
		String verifyString = hasher.hash(string);
		return user.getEmail() + "/" + verifyString;
	}

	public boolean verify(String email, String hash) {
		if (!userRepository.existsUserWithEmail(email))
			return false;

		String verifyString = email + "/" + hash;

		DbUser user = userRepository.selectUserByEmail(email);
		String currVerifyString = createVerifyString(user);
		if (currVerifyString.equalsIgnoreCase(verifyString)) {
			userRepository.updateUserVerified(user.getId(), true);
			return true;
		}

		return false;
	}

	public DbUserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(DbUserRepository userRepository) {
		this.userRepository = userRepository;
	}

}
