package com.github.shemhazai.mprw.repo.impl;

import com.github.shemhazai.mprw.domain.RiverStatus;
import com.github.shemhazai.mprw.repo.RiverStatusRepository;
import com.github.shemhazai.mprw.repo.mapper.RiverStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
public class RiverStatusRepositoryImpl implements RiverStatusRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public RiverStatusRepositoryImpl() {

    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RiverStatus selectRiverStatusById(int id) {
        try {
            String sql = "select * from riverStatus where id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new RiverStatusMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("RiverStatus with id: " + id + " doesn't exist!");
        }
    }

    @Override
    public List<RiverStatus> selectAllRiverStatuses() {
        String sql = "select * from riverStatus";
        return jdbcTemplate.query(sql, new RiverStatusMapper());
    }

    @Override
    public List<RiverStatus> selectAllRiverStatusesByRiverId(int riverId) {
        String sql = "select * from riverStatus where riverId = ? order by date desc";
        return jdbcTemplate.query(sql, new Object[]{riverId}, new RiverStatusMapper());
    }

    @Override
    public List<RiverStatus> selectLastRiverStatusesByRiverIdLimit(int riverId, int limit) {
        String sql = "select * from riverStatus where riverId = ? order by date desc limit ?";
        return jdbcTemplate.query(sql, new Object[]{riverId, limit}, new RiverStatusMapper());
    }

    @Override
    public List<RiverStatus> selectLastAverageRiverStatusesByRiverIdWithIntervalLimit(int riverId,
                                                                                      String interval, int limit) {
        try {
            String sql = buildQueryForLastAverageRiverStatus(riverId, interval, limit);
            return jdbcTemplate.query(sql, new Object[]{riverId, limit}, new RiverStatusMapper());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    private String buildQueryForLastAverageRiverStatus(int riverId, String interval, int limit)
            throws IllegalArgumentException {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, riverId, date, round(avg(level), 0) "
                + "as level from riverStatus where riverId = ? group by ");

        if (interval.equalsIgnoreCase("month")) {
            sql.append("concat(year(date), '/', month(date))");
        } else if (interval.equalsIgnoreCase("week")) {
            sql.append("concat(year(date), '/', week(date))");
        } else if (interval.equalsIgnoreCase("day")) {
            sql.append("concat(year(date), '/', month(date), '/', day(date))");
        } else {
            throw new IllegalArgumentException("Bad interval: " + interval);
        }

        sql.append(" order by date desc limit ?");
        return sql.toString();
    }

    @Override
    public boolean existsRiverStatusWithRiverIdAndDate(int riverId, Date date) {
        String sql = "select count(*) from riverStatus where riverId = ? and date = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{riverId, date}, Integer.class) != 0;
    }

    @Override
    @Transactional
    public RiverStatus createRiverStatus(int riverId) {
        String sql = "insert into riverStatus (riverId) values (?)";
        jdbcTemplate.update(sql, new Object[]{riverId});

        sql = "select max(id) from riverStatus";
        int id = jdbcTemplate.queryForObject(sql, Integer.class);
        return selectRiverStatusById(id);
    }

    @Override
    public void updateRiverStatus(int id, RiverStatus riverStatus) {
        String sql = "update riverStatus set riverId = ?, date = ?, level = ? where id = ?";
        jdbcTemplate.update(sql,
                new Object[]{riverStatus.getRiverId(), riverStatus.getDate(), riverStatus.getLevel(), id});
    }

    @Override
    public void deleteRiverStatus(int id) {
        String sql = "delete from riverStatus where id = ?";
        jdbcTemplate.update(sql, new Object[]{id});
    }
}
