package com.task.meeting.models;

import java.time.LocalDateTime;
import java.util.Date;

public class Participant {
    private Date date;
    private long PersonID;

    public Participant(long personID, Date date) {
        this.date = date;
        PersonID = personID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getPersonID() {
        return PersonID;
    }

    public void setPersonID(long personID) {
        PersonID = personID;
    }
}
