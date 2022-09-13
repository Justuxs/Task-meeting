package com.task.meeting.models;


import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class Meeting {

    private int id;
    @NotBlank(message = "Name is mandatory")
    private String Name;
    private long ResponsiblePerson;
    @NotBlank(message = "Description is mandatory")
    private String Description;
    //@NotBlank(message = "Category is mandatory")
    private CategoryEnum Category;
    // @NotBlank(message = "Type is mandatory")
    private TypeEnum Type;
    //@NotBlank(message = "StartDate is mandatory")
    private Date StartDate;
    //@NotBlank(message = "EndDate is mandatory")
    private Date EndDate;
    private final List < Participant > Participants = new ArrayList < > ();

    public Meeting() {}

    public Meeting(int id, String name, long responsiblePerson, String description, CategoryEnum category, TypeEnum type, Date startDate, Date endDate) {
        this.id = id;
        Name = name;
        ResponsiblePerson = responsiblePerson;
        Description = description;
        Category = category;
        Type = type;
        StartDate = startDate;
        EndDate = endDate;
    }

    public enum CategoryEnum {
        CodeMonkey,
        Hub,
        Short,
        TeamBuilding
    }

    public enum TypeEnum {
        Live,
        InPerson
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public long getResponsiblePerson() {
        return ResponsiblePerson;
    }

    public void setResponsiblePerson(long responsiblePerson) {
        ResponsiblePerson = responsiblePerson;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCategory() {
        return Category.name();
    }

    public void setCategory(CategoryEnum category) {
        Category = category;
    }

    public String getType() {
        return Type.name();
    }

    public void setType(TypeEnum type) {
        Type = type;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date startDate) {
        StartDate = startDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public List < Participant > getParticipants() {
        return Participants;
    }
    public void addPerson(long personID, Date date) {
        Participants.add(new Participant(personID, date));
    }

    public boolean personIsAdded(long personID) {
        if (Participants.stream().filter(x -> x.getPersonID() == personID).toList().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean removeParticipant(long id) {
        int index = IntStream.range(0, Participants.size())
                .filter(i -> Participants.get(i).getPersonID() == id)
                .findFirst()
                .orElse(-1);
        if (index == -1) return false;
        Participants.remove(index);
        return true;
    }
    public int countParticipant() {
        return Participants.size();
    }
}