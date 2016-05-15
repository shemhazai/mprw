package com.github.shemhazai.mprw.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.github.shemhazai.mprw.domain.HashedUser;

public class HashedUserMapper implements RowMapper<HashedUser> {

	@Override
	public HashedUser mapRow(ResultSet rs, int counter) throws SQLException {
		HashedUser user = new HashedUser();
		user.setId(rs.getInt("id"));
		user.setFirstName(rs.getString("firstName"));
		user.setLastName(rs.getString("lastName"));
		user.setEmail(rs.getString("email"));
		user.setPhone(rs.getString("phone"));
		user.setPassword(rs.getString("passwordHash"));
		user.setVerified(rs.getInt("verified") != 0);
		user.setMailAlert(rs.getInt("mailAlert") != 0);
		user.setPhoneAlert(rs.getInt("phoneAlert") != 0);
		return user;
	}

}
