package com.github.shemhazai.mprw.repo.mapper;

import com.github.shemhazai.mprw.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int counter) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("firstName"));
        user.setLastName(rs.getString("lastName"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setHashedPassword(rs.getString("passwordHash"));
        user.setVerified(rs.getInt("verified") != 0);
        user.setMailAlert(rs.getInt("mailAlert") != 0);
        user.setPhoneAlert(rs.getInt("phoneAlert") != 0);
        return user;
    }

}
