package com.github.shemhazai.mprw.service.impl;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.shemhazai.mprw.controller.UserController;
import com.github.shemhazai.mprw.domain.Token;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.domain.UserUpdateRequest;
import com.github.shemhazai.mprw.notify.MailNotifier;
import com.github.shemhazai.mprw.repo.UserRepository;
import com.github.shemhazai.mprw.service.UserService;
import com.github.shemhazai.mprw.utils.AuthenticationManager;
import com.github.shemhazai.mprw.utils.UserValidator;
import com.github.shemhazai.mprw.utils.VerificationManager;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private VerificationManager verificationManager;
  @Autowired
  private MailNotifier mailNotifier;

  private boolean isValid(Token token) {
    if (isNull(token))
      return false;
    return authenticationManager.isTokenRegistered(token);
  }

  private boolean isNull(Token token) {
    return token == null || token.getToken() == null || token.getEmail() == null;
  }

  @Override
  public User selectUserByToken(Token token) {
    if (isValid(token))
      return userRepository.selectUserByEmail(token.getEmail());
    return null;
  }

  @Override
  public Token createToken(User user) throws AuthenticationException {
    if (!isValid(user))
      throw new AuthenticationException("User not valid!");
    return authenticationManager.createAndRegisterToken(user);
  }

  private boolean isValid(User user) {
    if (isNull(user)) {
      System.out.println(user);
      System.out.println("NULLL");
      return false;
    }

    if (!userExists(user.getEmail()))
      return false;

    return isCorrectPassword(user);
  }

  private boolean userExists(String email) {
    return userRepository.existsUserWithEmail(email);
  }

  private boolean isNull(User user) {
    return user == null || user.getEmail() == null || user.getHashedPassword() == null;
  }

  private boolean isCorrectPassword(User user) {
    User u = userRepository.selectUserByEmail(user.getEmail());
    return u.getHashedPassword().equals(user.getHashedPassword());
  }

  @Override
  public String saveUser(User user) {
    if (!validateUser(user) || userExists(user.getEmail()))
      return UserController.FALSE;

    User emptyUser = userRepository.createUser(user.getEmail());
    userRepository.updateUser(emptyUser.getId(), user);
    return UserController.TRUE;
  }

  private boolean validateUser(User user) {
    return new UserValidator().validate(user);
  }

  @Override
  public String sendVerifyLink(Token token, String scheme, String serverName, String serverPort) {
    if (!isValid(token) || !userExists(token.getEmail()))
      return UserController.FALSE;

    User user = userRepository.selectUserByEmail(token.getEmail());
    String link = createLink(user, scheme, serverName, serverPort);
    mailNotifier.sendVerifyLink(token.getEmail(), link);

    return UserController.TRUE;
  }

  private String createLink(User user, String scheme, String serverName, String serverPort) {
    String verifyString = verificationManager.createVerifyString(user);
    return String.format("%s://%s:%s/mprw/rest/user/verify/%s", scheme, serverName, serverPort,
        verifyString);
  }

  @Override
  public String verify(String email, String hash) {
    if (verificationManager.verify(email, hash))
      return "Konto zostalo aktywowane!";
    return "Blad! Konto nie istnieje.";
  }

  @Override
  public String updateUser(UserUpdateRequest request) {
    if (isNull(request) || !userExists(request.getLoginEmail()))
      return UserController.FALSE;

    User user = new User();
    user.setEmail(request.getLoginEmail());
    user.setPassword(request.getLoginPassword());

    if (!isCorrectPassword(user))
      return UserController.FALSE;

    int fieldsToUpdate = request.countFieldsToUpdate();
    int validatedFields = request.countValidatedFields();

    if (validatedFields != fieldsToUpdate)
      return UserController.FALSE;

    request.updateUserFromRepository(userRepository);
    return UserController.TRUE;
  }

  private boolean isNull(UserUpdateRequest request) {
    return request == null || request.getLoginEmail() == null || request.getLoginPassword() == null;
  }

  @Override
  public boolean isVerified(String email) {
    email = email.trim();
    if (!userExists(email))
      return false;

    User user = userRepository.selectUserByEmail(email);
    return user.isVerified();
  }

  @Override
  public boolean isTokenRegistered(Token token) {
    return authenticationManager.isTokenRegistered(token);
  }

  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public AuthenticationManager getAuthenticationManager() {
    return authenticationManager;
  }

  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  public MailNotifier getMailNotifier() {
    return mailNotifier;
  }

  public void setMailNotifier(MailNotifier mailNotifier) {
    this.mailNotifier = mailNotifier;
  }

  public VerificationManager getVerificationManager() {
    return verificationManager;
  }

  public void setVerificationManager(VerificationManager verificationManager) {
    this.verificationManager = verificationManager;
  }
}
