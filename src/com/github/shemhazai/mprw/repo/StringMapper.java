package com.github.shemhazai.mprw.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StringMapper implements RowMapper<String> {
	@Override
	public String mapRow(ResultSet rs, int c) throws SQLException {
		return rs.getString(1);
	}
}
