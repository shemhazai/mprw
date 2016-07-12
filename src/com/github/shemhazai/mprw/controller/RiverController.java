package com.github.shemhazai.mprw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.RiverStatus;
import com.github.shemhazai.mprw.repo.RiverRepository;
import com.github.shemhazai.mprw.repo.RiverStatusRepository;

@RestController
@RequestMapping("/rest/river")
public class RiverController {

  @Autowired
  private RiverRepository riverRepository;
  @Autowired
  private RiverStatusRepository riverStatusRepository;

  @RequestMapping(value = "/selectAllRivers", method = RequestMethod.GET)
  public List<River> selectAllRivers() {
    return riverRepository.selectAllRivers();
  }

  @RequestMapping(value = "/selectRiversInDanger", method = RequestMethod.GET)
  public List<River> selectRiversInDanger() {
    return riverRepository.selectRiversInDanger();
  }

  @RequestMapping(value = "/selectAllRiverStatusesByRiverId/{riverId}", method = RequestMethod.GET)
  public List<RiverStatus> selectAllRiverStatusesByRiverId(@PathVariable int riverId) {
    return riverStatusRepository.selectAllRiverStatusesByRiverId(riverId);
  }

  @RequestMapping(value = "/selectLastRiverStatusesByRiverId/{riverId}/Limit/{limit}",
      method = RequestMethod.GET)
  public List<RiverStatus> selectLastRiverStatusesByRiverIdLimit(@PathVariable int riverId,
      @PathVariable int limit) {
    return riverStatusRepository.selectLastRiverStatusesByRiverIdLimit(riverId, limit);
  }

  @RequestMapping(
      value = "/selectLastAverageRiverStatusesByRiverId/{riverId}/WithInterval/{interval}/Limit/{limit}",
      method = RequestMethod.GET)
  public List<RiverStatus> selectLastAverageRiverStatusesByRiverIdWithIntervalLimit(
      @PathVariable int riverId, @PathVariable String interval, @PathVariable int limit) {
    return riverStatusRepository.selectLastAverageRiverStatusesByRiverIdWithIntervalLimit(riverId,
        interval, limit);
  }

  public RiverRepository getRiverRepository() {
    return riverRepository;
  }

  public void setRiverRepository(RiverRepository riverRepository) {
    this.riverRepository = riverRepository;
  }

  public RiverStatusRepository getRiverStatusRepository() {
    return riverStatusRepository;
  }

  public void setRiverStatusRepository(RiverStatusRepository riverStatusRepository) {
    this.riverStatusRepository = riverStatusRepository;
  }

}
