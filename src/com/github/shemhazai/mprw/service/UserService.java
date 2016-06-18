package com.github.shemhazai.mprw.service;

import com.github.shemhazai.mprw.domain.Token;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.domain.UserUpdateRequest;

public interface UserService {
	public User selectUserByToken(Token token);

	public String createToken(User user);

	public String createUser(User user);

	public String createVerifyLink(Token token, String scheme, String serverName, String serverPort);

	public String verify(String email, String verifyString);

	public String updateUser(UserUpdateRequest request);

	public String isVerified(String email);

	public String isTokenActive(String tokenHash);
}
