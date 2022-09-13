package com.task.meeting.repositories;

import com.google.gson.*;
import com.task.meeting.models.Meeting;
import com.task.meeting.models.Person;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PersonRepository {
    private static final String FileName= "Persons.txt";
    private static Gson gson = new Gson_().gson;


    public static List<Person> Read() {
        List<Person> allPersons= new ArrayList<Person>();
        var persons= gson.fromJson(InOut.Read(FileName), Person[].class);
        if(persons != null)allPersons.addAll(Arrays.asList(persons));
        return allPersons;
    }
    public static void Save(List<Person> allPersons) {
        String json = gson.toJson(allPersons);
        InOut.Write(FileName,json);
    }
}
