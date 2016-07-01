package com.github.shemhazai.mprw.service;

import javax.security.sasl.AuthenticationException;

import com.github.shemhazai.mprw.domain.Token;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.domain.UserUpdateRequest;

public interface UserService {
	public User selectUserByToken(Token token);

	public Token createToken(User user) throws AuthenticationException;

	public String saveUser(User user);

	public String sendVerifyLink(Token token, String scheme, String serverName,
			String serverPort);

	public String verify(String email, String verifyString);

	public String updateUser(UserUpdateRequest request);

	public boolean isVerified(String email);

	public boolean isTokenRegistered(Token token);
}
