package com.github.shemhazai.mprw.repo.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.github.shemhazai.mprw.domain.RiverStatus;

public class RiverStatusMapper implements RowMapper<RiverStatus> {

  @Override
  public RiverStatus mapRow(ResultSet rs, int counter) throws SQLException {
    RiverStatus riverStatus = new RiverStatus();
    riverStatus.setId(rs.getInt("id"));
    riverStatus.setRiverId(rs.getInt("riverId"));
    riverStatus.setDate(parseDate(rs));
    riverStatus.setLevel(rs.getInt("level"));
    return riverStatus;
  }

  private Date parseDate(ResultSet rs) throws SQLException {
    try {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String date = rs.getString("date");
      return dateFormat.parse(date);
    } catch (Exception e) {
      return new Date();
    }
  }
}
