package com.github.shemhazai.mprw.repo.mapper;

import com.github.shemhazai.mprw.domain.River;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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
