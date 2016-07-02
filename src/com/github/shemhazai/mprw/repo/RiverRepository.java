package com.github.shemhazai.mprw.repo;

import java.util.List;

import com.github.shemhazai.mprw.domain.River;

public interface RiverRepository {
	public void createRiverTableIfNotExists();

	public River selectRiverById(int id);

	public River selectRiverByName(String name);

	public List<River> selectAllRivers();

	public List<River> selectRiversInDanger();

	public River createRiver(String name);

	public void updateRiver(int id, River river);

	public void deleteRiver(int id);
}