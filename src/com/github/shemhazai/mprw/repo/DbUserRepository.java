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

	public void deleteUser(int id);
}
