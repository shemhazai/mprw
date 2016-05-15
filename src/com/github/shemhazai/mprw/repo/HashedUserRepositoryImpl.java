package com.github.shemhazai.mprw.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.shemhazai.mprw.domain.HashedUser;

@Repository
public class HashedUserRepositoryImpl implements HashedUserRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public HashedUserRepositoryImpl() {

	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void createUserTableIfNotExists() {
		String sql = "create table if not exists `user` (`id` int(11) auto_increment not null,"
				+ "`firstName` varchar(32), `lastName` varchar(32),`email` varchar(64),"
				+ "`phone` varchar(32),`passwordHash` varchar(512), `verified` int(2),"
				+ "`mailAlert` int(2), `phoneAlert` int(2), primary key(`id`)"
				+ ") default character set utf8 default collate utf8_general_ci";
		jdbcTemplate.update(sql);
	}

	@Override
	public HashedUser selectUserById(int id) {
		try {
			String sql = "select * from user where id = ?";
			return jdbcTemplate.queryForObject(sql, new Object[] { id }, new HashedUserMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public HashedUser selectUserByEmail(String email) {
		try {
			String sql = "select * from user where email = ?";
			return jdbcTemplate.queryForObject(sql, new Object[] { email }, new HashedUserMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<HashedUser> selectAllUsers() {
		String sql = "select * from user";
		return jdbcTemplate.query(sql, new HashedUserMapper());
	}

	@Override
	public List<HashedUser> selectUsersWithEmailAlert() {
		String sql = "select * from user where mailAlert != 0 and verified != 0";
		return jdbcTemplate.query(sql, new HashedUserMapper());
	}

	@Override
	public boolean existsUserWithEmail(String email) {
		String sql = "select count(*) from user where email = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] { email }, Integer.class) != 0;
	}

	@Override
	@Transactional
	public HashedUser createUser(String email) {
		String sql = "insert into user (email) values (?)";
		jdbcTemplate.update(sql, new Object[] { email });

		sql = "select max(id) from user";
		int id = jdbcTemplate.queryForObject(sql, Integer.class);
		return selectUserById(id);
	}

	@Override
	public void updateUser(int id, HashedUser user) {
		String sql = "update user set firstName = ?, lastName = ?, email = ?, phone = ?,"
				+ "passwordHash = ?, verified = ?, mailAlert = ?, phoneAlert = ? where id = ?";
		jdbcTemplate.update(sql,
				new Object[] { user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(),
						user.getPassword(), user.isVerified() ? 1 : 0, user.isMailAlert() ? 1 : 0,
						user.isPhoneAlert() ? 1 : 0, id });
	}

	@Override
	public void deleteUser(int id) {
		String sql = "delete from user where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
	}
}
