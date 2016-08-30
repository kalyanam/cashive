package com.cashive.dto;

import java.util.Date;

/**
 * Created by mkalyan on 8/27/16.
 */
public class ActiveGroupInfo {
    private Integer groupId;
    private Integer currentTerm;
    private Integer participantSize;
    private Date activatedDate;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(Integer currentTerm) {
        this.currentTerm = currentTerm;
    }

    public Integer getParticipantSize() {
        return participantSize;
    }

    public void setParticipantSize(Integer participantSize) {
        this.participantSize = participantSize;
    }

    public Date getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(Date activatedDate) {
        this.activatedDate = activatedDate;
    }
}
