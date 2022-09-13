package com.task.meeting.services;

import com.task.meeting.models.Person;
import com.task.meeting.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@Service
public class PersonService {

    private static List < Person > AllPersons = new ArrayList < Person > ();
    private static AtomicLong ID_GENERATOR = new AtomicLong(1);
    private static Long UserID = -1L;
    private static boolean refreshed = false;

    public PersonService() {
        refresh();
        if (AllPersons.isEmpty()) ID_GENERATOR = new AtomicLong(1);
        else {
            Person lastId = AllPersons.stream().max(Comparator.comparing(Person::getId)).get();
            ID_GENERATOR = new AtomicLong(lastId.getId() + 1);
        }
    }
    public static List < Person > GetAllPersons() {
        refresh();
        return AllPersons;
    }

    public static Person GetPerson(Long id) {
        refresh();
        Long index = LongStream.range(0, AllPersons.size())
                .filter(i -> AllPersons.get((int) i).getId() == id)
                .findFirst()
                .orElse(-1);
        if (index == -1) return null;
        return AllPersons.get(Math.toIntExact(index));
    }

    public static Boolean isExist(Person person) {
        refresh();
        Long index = LongStream.range(0, AllPersons.size())
                .filter(i -> AllPersons.get((int) i).equals(person))
                .findFirst()
                .orElse(-1);
        return !(index == -1);
    }

    public static Person GetUser() {
        return GetPerson(UserID);
    }

    public static long GetUserID() {
        return UserID;
    }

    public static long SavePerson(Person person) {
        if (person == null) {
            return -1;
        }
        person.setId((int) ID_GENERATOR.getAndIncrement());
        AllPersons.add(person);
        PersonRepository.Save(AllPersons);
        setRefresh();
        return person.getId();
    }

    public static boolean Login(Person person) {
        refresh();
        Long index = LongStream.range(0, AllPersons.size())
                .filter(i -> AllPersons.get((int) i).getId() == person.getId() &&
                        AllPersons.get((int) i).getName().toUpperCase(Locale.ROOT).equals(person.getName().toUpperCase(Locale.ROOT)) &&
                        AllPersons.get((int) i).getSurName().toUpperCase(Locale.ROOT).equals(person.getSurName().toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElse(-1);

        if (index == -1) {
            UserID = -1L;
            return false;
        }
        UserID = person.getId();
        return true;
    }

    public static boolean Delete(Long id) {
        refresh();
        if (AllPersons.stream().filter(x -> x.getId() == id).findFirst().isEmpty()) return false;
        AllPersons.removeIf(e -> e.getId() == id);
        PersonRepository.Save(AllPersons);
        setRefresh();
        return true;
    }


    private static void refresh() {
        if (!refreshed) {
            AllPersons = PersonRepository.Read();
            refreshed = true;
        }
    }

    private static void setRefresh() {
        refreshed = false;
    }

    public boolean Logout() {
        if (isLoged()) {
            UserID = -1l;
            return true;
        }
        return false;
    }

    public boolean isLoged() {
        return UserID != -1l;
    }
}