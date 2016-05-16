package com.github.shemhazai.mprw.repo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.shemhazai.mprw.domain.RiverStatus;

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
	public void createRiverStatusTableIfNotExists() {
		String sql = "create table if not exists `riverStatus` ( "
				+ "`id` int(11) auto_increment not null, `riverId` int(11) not null, "
				+ "`date` datetime, `level` int(11), primary key(`id`), "
				+ "foreign key(`riverId`) references `river`(`id`))"
				+ " default character set utf8 default collate utf8_general_ci";
		jdbcTemplate.update(sql);

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
	public List<RiverStatus> selectAllRiverStatusesByRiverId(int riverId) {
		String sql = "select * from riverStatus where riverId = ? order by date desc";
		return jdbcTemplate.query(sql, new Object[] { riverId }, new RiverStatusMapper());
	}

	@Override
	public List<RiverStatus> selectLastRiverStatusesByRiverIdLimit(int riverId, int limit) {
		String sql = "select * from riverStatus where riverId = ? order by date desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { riverId, limit }, new RiverStatusMapper());
	}

	@Override
	public boolean existsRiverStatusWithRiverIdAndDate(int riverId, Date date) {
		String sql = "select count(*) from riverStatus where riverId = ? and date = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] { riverId, date }, Integer.class) != 0;
	}

	@Override
	@Transactional
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
