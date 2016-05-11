package com.github.shemhazai.mprw.data;

import java.util.List;

import com.github.shemhazai.mprw.domain.River;

public interface DataAnalizer {
	public boolean isAlertLevelReached();
	public boolean isFloodLevelReached();
	
	public List<River> getRiversWithAlertLevelReached();
	public List<River> getRiversWithFloodLevelReached();
}
