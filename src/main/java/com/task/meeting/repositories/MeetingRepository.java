package com.task.meeting.repositories;

import com.google.gson.*;
import com.task.meeting.models.Meeting;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeetingRepository {

    private static final String FileName= "Meetings.txt";
    private static Gson gson = new Gson_().gson;


    public static List<Meeting> Read() {
        List<Meeting> allMeetings= new ArrayList<Meeting>();
        var meetings=gson.fromJson(InOut.Read(FileName), Meeting[].class);
        if(meetings != null)allMeetings.addAll(Arrays.asList(meetings));
        return allMeetings;
    }

    public static void Save(List<Meeting> allMeetings) {
        String json = gson.toJson(allMeetings);
        InOut.Write(FileName,json);
    }
}
