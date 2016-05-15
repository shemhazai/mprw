package com.github.shemhazai.mprw.repo;

import java.util.Date;
import java.util.List;

import com.github.shemhazai.mprw.domain.RiverStatus;

public interface RiverStatusRepository {
	public void createRiverStatusTableIfNotExists();

	public RiverStatus selectRiverStatusById(int id);

	public RiverStatus selectLastRiverStatusByRiverId(int riverId);

	public List<RiverStatus> selectAllRiverStatuses();

	public List<RiverStatus> selectRiverStatusesByRiverId(int riverId);

	public boolean existsRiverStatusWithRiverIdAndDate(int riverId, Date date);

	public RiverStatus createRiverStatus(int riverId);

	public void updateRiverStatus(int id, RiverStatus riverStatus);

	public void deleteRiverStatus(int id);
}
