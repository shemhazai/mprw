package com.github.shemhazai.mprw.repo;

import java.util.Date;
import java.util.List;

import com.github.shemhazai.mprw.domain.RiverStatus;

public interface RiverStatusRepository {
	public void createRiverStatusTableIfNotExists();

	public RiverStatus selectRiverStatusById(int id);

	public List<RiverStatus> selectAllRiverStatuses();

	public List<RiverStatus> selectAllRiverStatusesByRiverId(int riverId);

	public List<RiverStatus> selectLastRiverStatusesByRiverIdLimit(int riverId, int limit);

	public List<RiverStatus> selectLastAverageRiverStatusesByRiverIdWithIntervalLimit(int riverId, String interval, int limit);

	public boolean existsRiverStatusWithRiverIdAndDate(int riverId, Date date);

	public RiverStatus createRiverStatus(int riverId);

	public void updateRiverStatus(int id, RiverStatus riverStatus);

	public void deleteRiverStatus(int id);
}
