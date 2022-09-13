post http://localhost:8080/person/create  -- to create person
  {
    "name": Vardenis"
    "surName": "Pavardenis"
  }
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
post http://localhost:8080/person/login -- to login

  {
    "id": 1,
    "name": "Vardenis"
    "surName": "Pavardenis"
  }

@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
post http://localhost:8080/person/meeting/create -- to create meeting

    {
        "name": "Meeting",
        "type": "Live",
        "description": "My meeting",
        "endDate": "2022-09-12T19:50:14",
        "startDate": "2022-09-13T19:50:14",
        "category": "Hub"
    }
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
http://localhost:8080/person/logout - logout
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
post http://localhost:8080/person/meeting/add/1
new person ->    
{
        "name": "Jonas",
        "surName": "Jonaitis"
}
existing person ->
  {
    "id": 2,
    "name": "Vardenis1",
    "surName": "Pavardenis"
  }

@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
delete http://localhost:8080/person/meeting/1 -delete meeting

@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
post http://localhost:8080/person/meeting/remove/1 remove from meeting with id=1 perosn ->
    {
        "id": 3,
        "name": "Vardenis1",
        "surName": "Pavardenis"
    }
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
get http://localhost:8080/person/meeting?responsiblePerson=2  -get responsiblePerson.ID=2  all meetings 
get http://localhost:8080/person/meeting?attendees=2  -get all meetings with more than 2 attendees
get http://localhost:8080/person/meeting?type=live  -get all meetings with "live" type
get http://localhost:8080/person/meeting?type=live&category=hub  get all meetings with "live" type and "hub" category'
...


