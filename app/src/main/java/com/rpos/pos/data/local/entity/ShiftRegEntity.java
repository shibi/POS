package com.rpos.pos.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shiftRegTable")
public class ShiftRegEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private int shiftType;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private float beginningCash;
    private float endingCash;
    private String status;
    private long timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShiftType() {
        return shiftType;
    }

    public void setShiftType(int shiftType) {
        this.shiftType = shiftType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public float getBeginningCash() {
        return beginningCash;
    }

    public void setBeginningCash(float beginningCash) {
        this.beginningCash = beginningCash;
    }

    public float getEndingCash() {
        return endingCash;
    }

    public void setEndingCash(float endingCash) {
        this.endingCash = endingCash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
