package com.github.shemhazai.mprw.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.repo.UserRepository;

@Component
public class VerificationManager {

	@Autowired
	private UserRepository userRepository;

	public synchronized String createVerifyString(User user) {
		String string = user.getId() + ":" + user.getEmail();
		HashGenerator hasher = new HashGenerator();
		String verifyString = hasher.hash(string);
		return user.getEmail() + "/" + verifyString;
	}

	public boolean verify(String email, String hash) {
		if (!userRepository.existsUserWithEmail(email))
			return false;

		String verifyString = email + "/" + hash;

		User user = userRepository.selectUserByEmail(email);
		String currVerifyString = createVerifyString(user);
		if (currVerifyString.equalsIgnoreCase(verifyString)) {
			userRepository.updateUserVerified(user.getId(), true);
			return true;
		}

		return false;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

}
