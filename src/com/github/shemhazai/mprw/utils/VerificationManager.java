package com.github.shemhazai.mprw.utils;

import java.util.List;

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
		return hasher.hash(string);
	}

	public synchronized void verify(String verifyString) {
		List<DbUser> users = userRepository.selectAllUsers();

		for (DbUser user : users) {
			String currVerifyString = createVerifyString(user);
			if (currVerifyString.equalsIgnoreCase(verifyString)) {
				user.setVerified(true);
				userRepository.updateUser(user.getId(), user);
				break;
			}
		}

	}

	public DbUserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(DbUserRepository userRepository) {
		this.userRepository = userRepository;
	}

}
