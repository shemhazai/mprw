package com.github.shemhazai.mprw.utils;

import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.github.shemhazai.mprw.domain.User;

public class UserValidator {
	public boolean validate(User user) {
		if (!validateEmail(user.getEmail()))
			return false;

		if (!validatePassword(user.getPassword()))
			return false;

		if (!validateName(user.getFirstName()))
			return false;

		if (!validateName(user.getLastName()))
			return false;

		if (!validatePhone(user.getPhone()))
			return false;

		return true;
	}

	public boolean validateEmail(String email) {
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

	public boolean validatePassword(String password) {
		return password != null && password.length() >= 6;
	}

	public boolean validateName(String part) {
		return part != null && part.length() != 0;
	}

	public boolean validatePhone(String phone) {
		return phone != null && Pattern.matches("(\\d){9}", phone);
	}

}
