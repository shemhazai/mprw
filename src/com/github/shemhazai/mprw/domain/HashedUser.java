package com.github.shemhazai.mprw.domain;

import java.io.Serializable;

public class HashedUser extends User implements Serializable {

	private static final long serialVersionUID = 8156996190839986481L;

	private int id;
	private boolean verified;

	public HashedUser() {

	}

	public int getId() {
		return id;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + id;
		result = prime * result + (verified ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HashedUser other = (HashedUser) obj;
		if (id != other.id)
			return false;
		if (verified != other.verified)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HashedUser [id=" + id + ", verified=" + verified + ", firstName=" + getFirstName() + ", lastName="
				+ getLastName() + ", email=" + getEmail() + ", password=" + getPassword() + ", phone=" + getPhone()
				+ ", verified=" + isVerified() + ", mailAlert=" + isMailAlert() + ", phoneAlert=" + isPhoneAlert()
				+ "]";

	}

	public User toUser() {
		User user = new User();
		user.setEmail(getEmail());
		user.setPhone(getPhone());
		user.setPassword("");
		user.setFirstName(getFirstName());
		user.setLastName(getLastName());
		user.setMailAlert(isMailAlert());
		user.setPhoneAlert(isPhoneAlert());
		return user;
	}

}