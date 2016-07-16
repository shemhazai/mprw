package com.github.shemhazai.mprw.service;

import javax.security.sasl.AuthenticationException;

import com.github.shemhazai.mprw.domain.Token;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.domain.UserContact;
import com.github.shemhazai.mprw.domain.UserUpdateRequest;

public interface UserService {
  User selectUserByToken(Token token);

  Token createToken(User user) throws AuthenticationException;

  String saveUser(User user);

  String sendVerifyLink(Token token, String scheme, String serverName, String serverPort);

  String verify(String email, String verifyString);

  String updateUser(UserUpdateRequest request);

  boolean isVerified(String email);

  boolean isTokenRegistered(Token token);

  boolean contact(UserContact contact);
}
