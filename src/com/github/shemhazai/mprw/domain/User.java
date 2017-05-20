package com.github.shemhazai.mprw.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.github.shemhazai.mprw.utils.HashGenerator;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -1095994647013771174L;
    @JsonProperty(access = Access.WRITE_ONLY)
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = Access.WRITE_ONLY)
    private String hashedPassword;
    private String phone;
    private boolean mailAlert;
    private boolean phoneAlert;
    @JsonProperty(access = Access.WRITE_ONLY)
    private boolean verified;

    public int getId() {
        return id;
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

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isMailAlert() {
        return mailAlert;
    }

    public boolean isPhoneAlert() {
        return phoneAlert;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setId(int id) {
        this.id = id;
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
        if (password != null) {
            hashedPassword = new HashGenerator().hash(password);
        }
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMailAlert(boolean mailAlert) {
        this.mailAlert = mailAlert;
    }

    public void setPhoneAlert(boolean phoneAlert) {
        this.phoneAlert = phoneAlert;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((hashedPassword == null) ? 0 : hashedPassword.hashCode());
        result = prime * result + id;
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + (mailAlert ? 1231 : 1237);
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((phone == null) ? 0 : phone.hashCode());
        result = prime * result + (phoneAlert ? 1231 : 1237);
        result = prime * result + (verified ? 1231 : 1237);
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
        User other = (User) obj;
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
        if (hashedPassword == null) {
            if (other.hashedPassword != null)
                return false;
        } else if (!hashedPassword.equals(other.hashedPassword))
            return false;
        if (id != other.id)
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (mailAlert != other.mailAlert)
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
        if (phoneAlert != other.phoneAlert)
            return false;
        if (verified != other.verified)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
                + email + ", password=" + password + ", hashedPassword=" + hashedPassword + ", phone="
                + phone + ", mailAlert=" + mailAlert + ", phoneAlert=" + phoneAlert + ", verified="
                + verified + "]";
    }

}
