package com.github.shemhazai.mprw.utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.github.shemhazai.mprw.domain.User;

public class UserValidator {
	public boolean validate(User user) {
		if (!validateEmail(user.getEmail()))
			return false;

		if (!validatePassword(user.getPassword()))
			return false;

		if (!validatePartOfName(user.getFirstName()))
			return false;

		if (!validatePartOfName(user.getLastName()))
			return false;

		return true;
	}

	private boolean validateEmail(String email) {
		if (email == null || email.length() == 0 || email.contains(" "))
			return false;

		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
			return true;
		} catch (AddressException ex) {
			return false;
		}
	}

	private boolean validatePassword(String password) {
		return password != null && password.length() >= 6;
	}

	private boolean validatePartOfName(String part) {
		return part != null && part.length() != 0;
	}

}
