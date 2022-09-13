package com.task.meeting.controllers;


import com.task.meeting.models.Meeting;
import com.task.meeting.models.Person;
import com.task.meeting.services.MeetingService;
import com.task.meeting.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequestMapping(value = "/person", produces = "application/json")
@RestController
public class PersonContorller {

    private final PersonService personService;
    private final MeetingService meetingService;

    @Autowired
    public PersonContorller(PersonService personService, MeetingService meetingService) {
        this.personService = personService;
        this.meetingService = meetingService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List < Person > getAllPersons() {
        return PersonService.GetAllPersons();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity < String > createPerson(@Valid @RequestBody Person perosn) {
        long id = PersonService.SavePerson(perosn);
        return id == -1 ? ResponseEntity.ok("Failed to create person") : ResponseEntity.ok("Person is created with id -> "+id);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity < String > login(@Valid @RequestBody Person person) {
        return PersonService.Login(person) ? ResponseEntity.ok(person.getName() + " you are logged in") : ResponseEntity.ok("Failed login");
    }
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity < String > logOut() {
        return personService.Logout() ? ResponseEntity.ok("You are logged out") : ResponseEntity.ok("Failed logout, maybe you weren't login");
    }

    @RequestMapping(value = "/meeting/{id}", method = RequestMethod.DELETE)
    public ResponseEntity < String > deletePerson(@PathVariable Long id) {
        if (!personService.isLoged()) return ResponseEntity.ok("Please login to delete meeting -> /person/login");
        Meeting meeting = MeetingService.GetMeeting(id);
        if (meeting == null) return ResponseEntity.ok("Meeting with ID: " + id + " does not exist");
        if (meeting.getResponsiblePerson() != personService.GetUserID()) return ResponseEntity.ok("This person is not responsible for meeting");
        return PersonService.Delete(id) ? ResponseEntity.ok("Meeting is deleted") : ResponseEntity.ok("Something wrong happened");
    }

    @RequestMapping(value = "/meeting/create", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity < String > createMeeting(@Valid @RequestBody Meeting meeting) {
        if (!personService.isLoged()) return ResponseEntity.ok("Please login to create meeting -> /person/login");
        if (meeting.getStartDate() == null || meeting.getEndDate() == null) return ResponseEntity.ok("Please give valid object");
        meeting.setResponsiblePerson(PersonService.GetUserID());
        MeetingService.SaveMeeting(meeting);
        return ResponseEntity.ok("Meeting is created");
    }

    //add person to meeting
    @RequestMapping(value = "/meeting/add/{meetingid}", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity < String > addPerson(@PathVariable int meetingid, @RequestBody Person person, @RequestParam(defaultValue = "", name = "date") String date) {
        Meeting meeting = MeetingService.GetMeeting((long) meetingid);
        ResponseEntity < String > answer;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Date date_;
        try {
            date_ = formatter.parse(date);
        } catch (ParseException e) {
            System.out.println("Bad date format " + date);
            date_ = new Date();
        }
        if (meeting == null) answer = ResponseEntity.ok("Meeting with ID: " + meetingid + " does not exist");
        else if (!personService.isExist(person)) {
            long id = personService.SavePerson(person);
            if (id == -1) answer = ResponseEntity.ok("This person is not exist and error happened creating it");
            else {
                meeting.addPerson(person.getId(), date_);
                if (meetingService.EditMeeting((long) meetingid, meeting))
                    answer = ResponseEntity.ok("This person is not exist, so new person was created with id->" + id + " and he is added at meeting");
                else {
                    answer = ResponseEntity.ok("This person is not exist, so new person was created with id->" + id + " error happened adding it");
                }
            }
        } else if (meeting.personIsAdded(person.getId())) answer = ResponseEntity.ok(person.getName() + " is already in this meeting");
        else if (meeting.getResponsiblePerson() == person.getId()) answer = ResponseEntity.ok("This person is responsible for this meeting, so he cannot be added");
        else if (meetingService.isBusy(person.getId(), meeting.getStartDate())) answer = ResponseEntity.ok(person.getName() + " is already in different meeting at this time");
        else {
            meeting.addPerson(person.getId(), date_);
            if (meetingService.EditMeeting((long) meetingid, meeting))
                answer = ResponseEntity.ok("This person is is added at meeting");
            else {
                answer = ResponseEntity.ok("Something wrong happened");
            }
        }
        return answer;
    }

    //remove person from meeting
    @RequestMapping(value = "/meeting/remove/{meetingId}", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity < String > removePerson(@PathVariable long meetingId, @RequestBody Person person) {
        Meeting meeting = MeetingService.GetMeeting(meetingId);
        ResponseEntity < String > answer = ResponseEntity.ok("Something wrong happened");;
        if (meeting == null) answer = ResponseEntity.ok("Meeting with ID: " + meetingId + " does not exist");
        else if (!personService.isExist(person)) {
            answer = ResponseEntity.ok("This person is not exist and he cannot be removed.");
        } else if (!meeting.personIsAdded(person.getId())) answer = ResponseEntity.ok("Person is not in this meeting");
        else if (meeting.removeParticipant(person.getId())) {
            if (meetingService.EditMeeting(meetingId, meeting)) answer = ResponseEntity.ok("Person is removed from meeting");
        }
        return answer;
    }

    //list meetings
    @RequestMapping(value = "/meeting", method = RequestMethod.GET)
    public List < Meeting > listMeetings(@RequestParam(defaultValue = "none", name = "description") String description,
                                         @RequestParam(defaultValue = "none", name = "person") String responsiblePerson,
                                         @RequestParam(defaultValue = "none", name = "startdate") String startdate,
                                         @RequestParam(defaultValue = "none", name = "enddate") String enddate,
                                         @RequestParam(defaultValue = "none", name = "category") String category,
                                         @RequestParam(defaultValue = "none", name = "type") String type,
                                         @RequestParam(defaultValue = "none", name = "attendees") String attendees) {
        var meetings = meetingService.GetAllMeetings();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        if (!description.equals("none") && !description.isEmpty()) {
            meetings = meetings.stream().filter(meeting -> meeting.getDescription().toLowerCase(Locale.ROOT).matches("(.*)" + description.toLowerCase(Locale.ROOT) + "(.*)"))
                    .collect(Collectors.toList());
        }
        if (!responsiblePerson.equals("none") && !responsiblePerson.isEmpty()) {
            try {
                int id = Integer.parseInt(responsiblePerson);
                meetings = meetings.stream().filter(meeting -> meeting.getResponsiblePerson() == id)
                        .collect(Collectors.toList());
            } catch (NumberFormatException ex) {}
        }
        if (!startdate.equals("none") && !startdate.isEmpty()) {
            try {
                Date date = formatter.parse(startdate);
                meetings = meetings.stream().filter(meeting -> meeting.getStartDate().before(date))
                        .collect(Collectors.toList());
            } catch (ParseException e) {}
        }
        //between
        if (!enddate.equals("none") && !enddate.isEmpty()) {
            try {
                Date date = formatter.parse(enddate);
                meetings = meetings.stream().filter(meeting -> meeting.getStartDate().after(date))
                        .collect(Collectors.toList());
            } catch (ParseException e) {}
        }
        if (!category.equals("none") && !category.isEmpty()) {
            meetings = meetings.stream().filter(meeting -> meeting.getCategory().toLowerCase(Locale.ROOT).equals(category.toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        if (!type.equals("none") && !type.isEmpty()) {
            meetings = meetings.stream().filter(meeting -> meeting.getType().toLowerCase(Locale.ROOT).equals(type.toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        if (!attendees.equals("none") && !attendees.isEmpty()) {
            try {
                int over = Integer.parseInt(attendees);
                meetings = meetings.stream().filter(meeting -> meeting.countParticipant() > over)
                        .collect(Collectors.toList());
            } catch (NumberFormatException ex) {}
        }
        return meetings;
    }
}