package com.github.shemhazai.mprw.domain;

import java.io.Serializable;

public class River implements Serializable {

    private static final long serialVersionUID = -5761937738283007797L;

    private int id;
    private String name;
    private String description;
    private int floodLevel;
    private int alertLevel;

    public River() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getFloodLevel() {
        return floodLevel;
    }

    public int getAlertLevel() {
        return alertLevel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFloodLevel(int floodLevel) {
        this.floodLevel = floodLevel;
    }

    public void setAlertLevel(int alertLevel) {
        this.alertLevel = alertLevel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + alertLevel;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + floodLevel;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        River other = (River) obj;
        if (alertLevel != other.alertLevel)
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (floodLevel != other.floodLevel)
            return false;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "River [id=" + id + ", name=" + name + ", description=" + description + ", floodLevel="
                + floodLevel + ", alertLevel=" + alertLevel + "]";
    }
}
