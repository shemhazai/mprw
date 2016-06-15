package com.github.shemhazai.mprw.repo;

import java.util.List;

import com.github.shemhazai.mprw.domain.DbUser;

public interface DbUserRepository {
	public void createUserTableIfNotExists();

	public DbUser selectUserById(int id);

	public DbUser selectUserByEmail(String email);

	public List<DbUser> selectAllUsers();

	public List<DbUser> selectUsersWithEmailAlert();

	public boolean existsUserWithEmail(String email);

	public DbUser createUser(String email);

	public void updateUser(int id, DbUser user);

	public void updateUserFirstName(int id, String firstName);

	public void updateUserLastName(int id, String lastName);

	public void updateUserEmail(int id, String email);

	public void updateUserPasswordHash(int id, String passwordHash);

	public void updateUserPhone(int id, String phone);

	public void updateUserMailAlert(int id, boolean mailAlert);

	public void updateUserPhoneAlert(int id, boolean phoneAlert);

	public void updateUserVerified(int id, boolean verified);

	public void deleteUser(int id);
}
