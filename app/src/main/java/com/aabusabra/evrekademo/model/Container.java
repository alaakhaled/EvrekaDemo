package com.aabusabra.evrekademo.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Container {

    private String key;
    private String containerID;
    private String lastCollectionDate;
    private int fullnessRate;
    private double container_latitude;
    private double container_longitude;

    public Container() {
    }

    public Container(String key, String containerID, String lastCollectionDate, int fullnessRate, double container_latitude, double container_longitude) {
        this.key = key;
        this.containerID = containerID;
        this.lastCollectionDate = lastCollectionDate;
        this.fullnessRate = fullnessRate;
        this.container_latitude = container_latitude;
        this.container_longitude = container_longitude;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getLastCollectionDate() {
        return lastCollectionDate;
    }

    public void setLastCollectionDate(String lastCollectionDate) {
        this.lastCollectionDate = lastCollectionDate;
    }

    public int getFullnessRate() {
        return fullnessRate;
    }

    public void setFullnessRate(int fullnessRate) {
        this.fullnessRate = fullnessRate;
    }

    public double getContainer_latitude() {
        return container_latitude;
    }

    public void setContainer_latitude(double container_latitude) {
        this.container_latitude = container_latitude;
    }

    public double getContainer_longitude() {
        return container_longitude;
    }

    public void setContainer_longitude(double container_longitude) {
        this.container_longitude = container_longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
