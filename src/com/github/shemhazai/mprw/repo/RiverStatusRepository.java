package com.github.shemhazai.mprw.repo;

import java.util.Date;
import java.util.List;

import com.github.shemhazai.mprw.domain.RiverStatus;

public interface RiverStatusRepository {
  void createRiverStatusTableIfNotExists();

  RiverStatus selectRiverStatusById(int id);

  List<RiverStatus> selectAllRiverStatuses();

  List<RiverStatus> selectAllRiverStatusesByRiverId(int riverId);

  List<RiverStatus> selectLastRiverStatusesByRiverIdLimit(int riverId, int limit);

  List<RiverStatus> selectLastAverageRiverStatusesByRiverIdWithIntervalLimit(int riverId,
      String interval, int limit);

  boolean existsRiverStatusWithRiverIdAndDate(int riverId, Date date);

  RiverStatus createRiverStatus(int riverId);

  void updateRiverStatus(int id, RiverStatus riverStatus);

  void deleteRiverStatus(int id);
}
