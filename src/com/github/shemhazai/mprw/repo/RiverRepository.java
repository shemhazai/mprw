package com.github.shemhazai.mprw.repo;

import com.github.shemhazai.mprw.domain.River;

import java.util.List;

public interface RiverRepository {
    River selectRiverById(int id);

    River selectRiverByName(String name);

    List<River> selectAllRivers();

    List<River> selectRiversInDanger();

    River createRiver(String name);

    void updateRiver(int id, River river);

    void deleteRiver(int id);
}
