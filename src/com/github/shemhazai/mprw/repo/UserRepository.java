package com.github.shemhazai.mprw.repo;

import com.github.shemhazai.mprw.domain.User;

import java.util.List;

public interface UserRepository {
    User selectUserById(int id);

    User selectUserByEmail(String email);

    List<User> selectAllUsers();

    List<User> selectUsersWithEmailAlert();

    boolean existsUserWithEmail(String email);

    User createUser(String email);

    void updateUser(int id, User user);

    void updateUserFirstName(int id, String firstName);

    void updateUserLastName(int id, String lastName);

    void updateUserEmail(int id, String email);

    void updateUserPasswordHash(int id, String passwordHash);

    void updateUserPhone(int id, String phone);

    void updateUserMailAlert(int id, boolean mailAlert);

    void updateUserPhoneAlert(int id, boolean phoneAlert);

    void updateUserVerified(int id, boolean verified);

    void deleteUser(int id);
}
