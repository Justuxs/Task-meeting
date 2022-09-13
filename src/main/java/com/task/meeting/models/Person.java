package com.task.meeting.models;

import javax.validation.constraints.NotBlank;

public class Person {
    private long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "surName is mandatory")
    private String surName;

    public Person(long id, String name, String surName) {
        this.id = id;
        this.name = name;
        this.surName = surName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String toString() {
        return ("name: "+this.name+" surName: "+ this.surName+" id: "+this.id);
    }

    public boolean equals(Person person){
        return (person.getName().equals(this.name) &&
                person.getSurName().equals(this.surName) &&
                person.getId() == this.id);
    }
}
