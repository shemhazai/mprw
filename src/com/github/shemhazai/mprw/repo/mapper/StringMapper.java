package com.github.shemhazai.mprw.repo.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringMapper implements RowMapper<String> {
    @Override
    public String mapRow(ResultSet rs, int c) throws SQLException {
        return rs.getString(1);
    }
}
