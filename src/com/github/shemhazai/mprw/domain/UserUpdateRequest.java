package com.github.shemhazai.mprw.domain;

import com.github.shemhazai.mprw.repo.UserRepository;
import com.github.shemhazai.mprw.utils.HashGenerator;
import com.github.shemhazai.mprw.utils.UserValidator;

public class UserUpdateRequest {
	private String loginEmail;
	private String loginPassword;

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phone;
	private Boolean phoneAlert;
	private Boolean mailAlert;

	public String getLoginEmail() {
		return loginEmail;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getPhone() {
		return phone;
	}

	public Boolean getPhoneAlert() {
		return phoneAlert;
	}

	public Boolean getMailAlert() {
		return mailAlert;
	}

	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPhoneAlert(Boolean phoneAlert) {
		this.phoneAlert = phoneAlert;
	}

	public void setMailAlert(Boolean mailAlert) {
		this.mailAlert = mailAlert;
	}

	public int countFieldsToUpdate() {
		int fieldsToUpdate = 0;

		if (email != null)
			fieldsToUpdate++;
		if (password != null)
			fieldsToUpdate++;
		if (firstName != null)
			fieldsToUpdate++;
		if (lastName != null)
			fieldsToUpdate++;
		if (phone != null)
			fieldsToUpdate++;
		if (phoneAlert != null)
			fieldsToUpdate++;
		if (mailAlert != null)
			fieldsToUpdate++;
		return fieldsToUpdate;
	}

	public int countValidatedFields() {
		UserValidator validator = new UserValidator();
		int validatedFields = 0;

		if (email != null && validator.validateEmail(email))
			validatedFields++;
		if (password != null && validator.validatePassword(password))
			validatedFields++;
		if (firstName != null && validator.validateName(firstName))
			validatedFields++;
		if (lastName != null && validator.validateName(lastName))
			validatedFields++;
		if (phone != null && validator.validatePhone(phone))
			validatedFields++;
		if (mailAlert != null)
			validatedFields++;
		if (phoneAlert != null)
			validatedFields++;
		return validatedFields;
	}

	public void updateUserFromRepository(UserRepository userRepository) {
		User user = userRepository.selectUserByEmail(loginEmail);

		if (email != null)
			userRepository.updateUserEmail(user.getId(), email);
		if (password != null) {
			HashGenerator hasher = new HashGenerator();
			userRepository.updateUserPasswordHash(user.getId(),
					hasher.hash(password));
		}
		if (firstName != null)
			userRepository.updateUserFirstName(user.getId(), firstName);
		if (lastName != null)
			userRepository.updateUserLastName(user.getId(), lastName);
		if (phone != null)
			userRepository.updateUserPhone(user.getId(), phone);
		if (phoneAlert != null)
			userRepository.updateUserPhoneAlert(user.getId(), phoneAlert);
		if (mailAlert != null)
			userRepository.updateUserMailAlert(user.getId(), mailAlert);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((loginEmail == null) ? 0 : loginEmail.hashCode());
		result = prime * result
				+ ((loginPassword == null) ? 0 : loginPassword.hashCode());
		result = prime * result
				+ ((mailAlert == null) ? 0 : mailAlert.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result
				+ ((phoneAlert == null) ? 0 : phoneAlert.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserUpdateRequest other = (UserUpdateRequest) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (loginEmail == null) {
			if (other.loginEmail != null)
				return false;
		} else if (!loginEmail.equals(other.loginEmail))
			return false;
		if (loginPassword == null) {
			if (other.loginPassword != null)
				return false;
		} else if (!loginPassword.equals(other.loginPassword))
			return false;
		if (mailAlert == null) {
			if (other.mailAlert != null)
				return false;
		} else if (!mailAlert.equals(other.mailAlert))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (phoneAlert == null) {
			if (other.phoneAlert != null)
				return false;
		} else if (!phoneAlert.equals(other.phoneAlert))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserUpdateRequest [loginEmail=" + loginEmail
				+ ", loginPassword=" + loginPassword + ", firstName="
				+ firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", phone=" + phone
				+ ", phoneAlert=" + phoneAlert + ", mailAlert=" + mailAlert
				+ "]";
	}

}
