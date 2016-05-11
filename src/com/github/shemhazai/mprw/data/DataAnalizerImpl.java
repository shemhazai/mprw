package com.github.shemhazai.mprw.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.RiverStatus;
import com.github.shemhazai.mprw.repo.AppRepository;

@Component
public class DataAnalizerImpl implements DataAnalizer {

	@Autowired
	private AppRepository repository;

	public DataAnalizerImpl(AppRepository repository) {
		this.repository = repository;
	}

	public DataAnalizerImpl() {

	}

	public AppRepository getRepository() {
		return repository;
	}

	public void setRepository(AppRepository repository) {
		this.repository = repository;
	}

	@Override
	public boolean isAlertLevelReached() {
		return !getRiversWithAlertLevelReached().isEmpty();
	}

	@Override
	public boolean isFloodLevelReached() {
		return !getRiversWithFloodLevelReached().isEmpty();
	}

	@Override
	public List<River> getRiversWithAlertLevelReached() {
		List<River> listOfRiver = new ArrayList<>();
		for (River river : repository.selectAllRivers()) {
			RiverStatus riverStatus = repository.selectLastRiverStatusByRiverId(river.getId());
			if (riverStatus != null && river.getAlertLevel() <= riverStatus.getLevel())
				listOfRiver.add(river);
		}
		return listOfRiver;
	}

	@Override
	public List<River> getRiversWithFloodLevelReached() {
		List<River> listOfRiver = new ArrayList<>();
		for (River river : repository.selectAllRivers()) {
			RiverStatus riverStatus = repository.selectLastRiverStatusByRiverId(river.getId());
			if (riverStatus != null && river.getFloodLevel() <= riverStatus.getLevel())
				listOfRiver.add(river);
		}
		return listOfRiver;
	}

}
