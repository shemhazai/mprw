package com.github.shemhazai.mprw.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.github.shemhazai.mprw.domain.River;

public class RiverMapper implements RowMapper<River> {

	@Override
	public River mapRow(ResultSet rs, int counter) throws SQLException {
		River river = new River();
		river.setId(rs.getInt("id"));
		river.setName(rs.getString("name"));
		river.setDescription(rs.getString("description"));
		river.setFloodLevel(rs.getInt("floodLevel"));
		river.setAlertLevel(rs.getInt("alertLevel"));
		return river;
	}
}
