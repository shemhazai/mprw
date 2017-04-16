package com.github.shemhazai.mprw.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.shemhazai.mprw.data.JsonDateSerializer;

public class RiverStatus implements Serializable {

  private static final long serialVersionUID = -557792504939639101L;

  private int id;
  private int riverId;
  @JsonSerialize(using = JsonDateSerializer.class)
  private Date date;
  private int level;

  public RiverStatus() {

  }

  public int getId() {
    return id;
  }

  public int getRiverId() {
    return riverId;
  }

  public Date getDate() {
    return date;
  }

  public int getLevel() {
    return level;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setRiverId(int riverId) {
    this.riverId = riverId;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + id;
    result = prime * result + level;
    result = prime * result + riverId;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RiverStatus other = (RiverStatus) obj;
    if (date == null) {
      if (other.date != null)
        return false;
    } else if (!date.equals(other.date))
      return false;
    if (id != other.id)
      return false;
    if (level != other.level)
      return false;
    if (riverId != other.riverId)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "RiverStatus [id=" + id + ", riverId=" + riverId + ", date=" + date + ", level=" + level
        + "]";
  }

}
