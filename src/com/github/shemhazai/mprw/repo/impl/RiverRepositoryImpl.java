package com.github.shemhazai.mprw.repo.impl;

import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.repo.RiverRepository;
import com.github.shemhazai.mprw.repo.mapper.RiverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RiverRepositoryImpl implements RiverRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public RiverRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RiverRepositoryImpl() {

    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public River selectRiverById(int id) {
        try {
            String sql = "select * from river where id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new RiverMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("River with id: " + id + " doesn't exist!");
        }
    }

    @Override
    public River selectRiverByName(String name) {
        try {
            String sql = "select * from river where name = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{name}, new RiverMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("River with name: " + name + " doesn't exist!");
        }
    }

    @Override
    public List<River> selectAllRivers() {
        String sql = "select * from river";
        return jdbcTemplate.query(sql, new RiverMapper());
    }

    @Override
    public List<River> selectRiversInDanger() {
        String sql = "select distinct * from river as r where alertLevel <= ( "
                + "select rs.level from riverStatus as rs where rs.riverId = r.id "
                + "order by rs.date desc limit 1);";
        return jdbcTemplate.query(sql, new RiverMapper());
    }

    @Override
    @Transactional
    public River createRiver(String name) {
        String sql = "insert into river (name) values (?)";
        jdbcTemplate.update(sql, new Object[]{name});

        sql = "select max(id) from river";
        int id = jdbcTemplate.queryForObject(sql, Integer.class);
        return selectRiverById(id);
    }

    @Override
    public void updateRiver(int id, River river) {
        String sql =
                "update river set name = ?, description = ?, alertLevel = ?, floodLevel = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{river.getName(), river.getDescription(),
                river.getAlertLevel(), river.getFloodLevel(), id});
    }

    @Override
    public void deleteRiver(int id) {
        String sql = "delete from river where id = ?";
        jdbcTemplate.update(sql, new Object[]{id});
    }

}
