package com.github.ghostbusters.ghosthouse.home.fragments;

/**
 * Created by javiosa on 19/04/18.
 */

public class GhostDevice {



    public long id;
    public String name;
    public String userId;
    public String latitude;
    public String longitude;

    public GhostDevice() {
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
