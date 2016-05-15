package com.github.shemhazai.mprw.repo;

import java.util.List;

import com.github.shemhazai.mprw.domain.HashedUser;

public interface HashedUserRepository {
	public void createUserTableIfNotExists();

	public HashedUser selectUserById(int id);

	public HashedUser selectUserByEmail(String email);

	public List<HashedUser> selectAllUsers();
	
	public List<HashedUser> selectUsersWithEmailAlert();

	public boolean existsUserWithEmail(String email);

	public HashedUser createUser(String email);

	public void updateUser(int id, HashedUser user);

	public void deleteUser(int id);
}
