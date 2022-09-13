package com.task.meeting.services;

import com.task.meeting.models.Meeting;
import com.task.meeting.models.Person;
import com.task.meeting.repositories.InOut;
import com.task.meeting.repositories.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Service
public class MeetingService {

    private static List<Meeting> AllMeetings = new ArrayList<Meeting>();
    private static AtomicLong ID_GENERATOR = new AtomicLong(1);
    private static boolean refreshed = false;

    public MeetingService(){
        refresh();
        if(AllMeetings.isEmpty()) ID_GENERATOR = new AtomicLong(1);
        else{
            Meeting lastId= AllMeetings.stream().max(Comparator.comparing(Meeting::getId)).get();
            ID_GENERATOR = new AtomicLong(lastId.getId()+1);
        }
    }

    //returns all meetings
    public static List<Meeting> GetAllMeetings(){
        refresh();
        return AllMeetings;
    }
    public static Meeting GetMeeting(Long id){
        refresh();
        Long index = LongStream.range(0, AllMeetings.size())
                .filter(i -> AllMeetings.get((int) i).getId() == id )
                .findFirst()
                .orElse(-1);
        if(index == -1) return null;
        return AllMeetings.get(Math.toIntExact(index));
    }

    public static void SaveMeeting(Meeting meeting) {
        if(meeting == null){
            System.err.println("Meeting is null");
            return;
        }
        meeting.setId((int) ID_GENERATOR.getAndIncrement());
        AllMeetings.add(meeting);
        MeetingRepository.Save(AllMeetings);
        setRefresh();
    }

    public static boolean isBusy(long personID, Date date){
        var meetings= AllMeetings.stream().filter(x -> x.personIsAdded(personID) && (date.after(x.getStartDate()) && x.getEndDate().before(date))).toList();
        if(meetings.isEmpty())return false;
        return true;
    }
    //saves one not Null meeting
    public static boolean EditMeeting(Meeting old_meeting,Meeting new_meeting) {
        refresh();
        if(new_meeting == null || old_meeting == null || AllMeetings.isEmpty()){
            return false;
        }
        AllMeetings.set(AllMeetings.indexOf(old_meeting),new_meeting);  //reikia if neranda
        MeetingRepository.Save(AllMeetings);
        setRefresh();
        return true;
    }

    public static boolean EditMeeting(Long id,Meeting new_meeting) {
        refresh();
        int index = IntStream.range(0, AllMeetings.size())
                .filter(i -> AllMeetings.get(i).getId() == id)
                .findFirst()
                .orElse(-1);

        if(index == -1 || id != new_meeting.getId())
            return false;

        AllMeetings.set(index,new_meeting);
        MeetingRepository.Save(AllMeetings);
        setRefresh();
        return true;
    }

    public static boolean Delete(Long id){
        refresh();
        if(AllMeetings.stream().filter(x -> x.getId() == id).findFirst().isEmpty()) return false;
        AllMeetings.removeIf(e -> e.getId() == id);
        MeetingRepository.Save(AllMeetings);
        setRefresh();
        return true;
    }

    public static void print(){
        for (var meet:AllMeetings
        ) {
            System.out.println(meet.getName());
        }
    }

    private static void refresh(){
        if(!refreshed){
            AllMeetings=MeetingRepository.Read();
            refreshed = true;
        }
    }

    private static void setRefresh(){
        refreshed = false;
    }
}
