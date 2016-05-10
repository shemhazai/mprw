package com.github.shemhazai.mprw.repo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.RiverStatus;

@Repository
public class MysqlRepository implements AppRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public MysqlRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public MysqlRepository() {

	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void createRiverTableIfNotExists() {
		String sql = "create table if not exists `river` (`id` int(11) auto_increment not null,"
				+ "`name` varchar(16), `description` varchar(256), `alertLevel` int(11),"
				+ "`floodLevel` int(11), primary key (`id`)) default character set utf8 "
				+ "default collate utf8_general_ci;";
		jdbcTemplate.update(sql);
	}

	@Override
	public void createRiverStatusTableIfNotExists() {
		String sql = "create table if not exists `riverStatus` ( "
				+ "`id` int(11) auto_increment not null, `riverId` int(11) not null, "
				+ "`date` datetime, `level` int(11), primary key(`id`), "
				+ "foreign key(`riverId`) references `river`(`id`))"
				+ " default character set utf8 default collate utf8_general_ci";
		jdbcTemplate.update(sql);

	}

	@Override
	public River selectRiverById(int id) {
		try {
			String sql = "select * from river where id = ?";
			return jdbcTemplate.queryForObject(sql, new Object[] { id }, new RiverMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public River selectRiverByName(String name) {
		try {
			String sql = "select * from river where name = ?";
			return jdbcTemplate.queryForObject(sql, new Object[] { name }, new RiverMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<River> selectAllRivers() {
		String sql = "select * from river";
		return jdbcTemplate.query(sql, new RiverMapper());
	}

	@Override
	public River createRiver(String name) {
		String sql = "insert into river (name) values (?)";
		jdbcTemplate.update(sql, new Object[] { name });

		sql = "select max(id) from river";
		int id = jdbcTemplate.queryForObject(sql, Integer.class);
		return selectRiverById(id);
	}

	@Override
	public void updateRiver(int id, River river) {
		String sql = "update river set name = ?, description = ?, alertLevel = ?, floodLevel = ? where id = ?";
		jdbcTemplate.update(sql, new Object[] { river.getName(), river.getDescription(), river.getAlertLevel(),
				river.getFloodLevel(), id });
	}

	@Override
	public void deleteRiver(int id) {
		String sql = "delete from river where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
	}

	@Override
	public RiverStatus selectRiverStatusById(int id) {
		try {
			String sql = "select * from riverStatus where id = ?";
			return jdbcTemplate.queryForObject(sql, new Object[] { id }, new RiverStatusMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<RiverStatus> selectAllRiverStatuses() {
		String sql = "select * from riverStatus";
		return jdbcTemplate.query(sql, new RiverStatusMapper());
	}

	@Override
	public List<RiverStatus> selectRiverStatusesByRiverId(int riverId) {
		String sql = "select * from riverStatus where riverId = ?";
		return jdbcTemplate.query(sql, new Object[] { riverId }, new RiverStatusMapper());
	}

	@Override
	public boolean existsRiverStatusWithRiverIdAndDate(int riverId, Date date) {
		String sql = "select count(*) from riverStatus where riverId = ? and date = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] { riverId, date }, Integer.class) != 0;
	}

	@Override
	public RiverStatus createRiverStatus(int riverId) {
		String sql = "insert into riverStatus (riverId) values (?)";
		jdbcTemplate.update(sql, new Object[] { riverId });

		sql = "select max(id) from riverStatus";
		int id = jdbcTemplate.queryForObject(sql, Integer.class);
		return selectRiverStatusById(id);
	}

	@Override
	public void updateRiverStatus(int id, RiverStatus riverStatus) {
		String sql = "update riverStatus set riverId = ?, date = ?, level = ? where id = ?";
		jdbcTemplate.update(sql,
				new Object[] { riverStatus.getRiverId(), riverStatus.getDate(), riverStatus.getLevel(), id });
	}

	@Override
	public void deleteRiverStatus(int id) {
		String sql = "delete from riverStatus where id = ?";
		jdbcTemplate.update(sql, new Object[] { id });
	}
}
