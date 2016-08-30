package com.cashive.dto;

import java.util.Date;

/**
 * Created by mkalyan on 8/27/16.
 */
public class Group {
    private Integer groupId;
    private String name;
    private Integer startedBy;
    private Date startDate;
    private Integer termLength;
    private String termFrequency;
    private Integer termAmount;
    private Integer maxParticipants;
    private Date createdDate;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartedBy() {
        return startedBy;
    }

    public void setStartedBy(Integer startedBy) {
        this.startedBy = startedBy;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getTermLength() {
        return termLength;
    }

    public void setTermLength(Integer termLength) {
        this.termLength = termLength;
    }

    public String getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(String termFrequency) {
        this.termFrequency = termFrequency;
    }

    public Integer getTermAmount() {
        return termAmount;
    }

    public void setTermAmount(Integer termAmount) {
        this.termAmount = termAmount;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
