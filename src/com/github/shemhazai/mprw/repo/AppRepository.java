package com.github.shemhazai.mprw.repo;

import java.util.Date;
import java.util.List;

import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.RiverStatus;
import com.github.shemhazai.mprw.domain.User;

public interface AppRepository {
	public void createRiverTableIfNotExists();
	public void createRiverStatusTableIfNotExists();
	public void createUserTableIfNotExists();
	
	public River selectRiverById(int id);
	public River selectRiverByName(String name);
	public List<River> selectAllRivers();
	
	public River createRiver(String name);
	public void updateRiver(int id, River river);
	public void deleteRiver(int id);
	
	public RiverStatus selectRiverStatusById(int id);
	public RiverStatus selectLastRiverStatusByRiverId(int riverId);
	public List<RiverStatus> selectAllRiverStatuses();
	public List<RiverStatus> selectRiverStatusesByRiverId(int riverId);
	
	public boolean existsRiverStatusWithRiverIdAndDate(int riverId, Date date);
	
	public RiverStatus createRiverStatus(int riverId);
	public void updateRiverStatus(int id, RiverStatus riverStatus);
	public void deleteRiverStatus(int id);
	
	public User selectUserById(int id);
	public User selectUserByEmail(String email);
	public List<User> selectAllUsers();
	
	public boolean existsUserWithEmail(String email);
	
	public User createUser(String email);
	public void updateUser(int id, User user);
	public void deleteUser(int id);
	
}