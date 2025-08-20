package com.example.indiwarenative;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

public class lesson {
    public int pos;
    public String teacher;
    public String subject;
    public String room;
    public LocalTime start;
    public LocalTime end;
    public boolean canceled;

    public lesson(int pos, String teacher, String subject, String room, LocalTime start, LocalTime end, boolean canceled) {
        this.teacher = teacher;
        this.subject = subject;
        this.room = room;
        this.start = start;
        this.end = end;
        this.pos = pos;
        this.canceled = canceled;
    }

    public  lesson () {
        this.pos = 0;
        this.subject = "error";
        this.room = "error";
        this.teacher = "error";
    }
}


