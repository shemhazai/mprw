package com.github.shemhazai.mprw.repo;

import java.util.List;

import com.github.shemhazai.mprw.domain.River;

public interface RiverRepository {
  void createRiverTableIfNotExists();

  River selectRiverById(int id);

  River selectRiverByName(String name);

  List<River> selectAllRivers();

  List<River> selectRiversInDanger();

  River createRiver(String name);

  void updateRiver(int id, River river);

  void deleteRiver(int id);
}
