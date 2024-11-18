Sport events api.
This is what i've managed to make in one day ( there are a lot of thing to be improved, but had no time for that ).

  java.version:17

To run the application:
mvn clean install 
mvn spring-boot:run

Endpoints examples:
GET http://localhost:8090/api/sport-events
GET http://localhost:8090/api/sport-events/1
GET http://localhost:8090/api/sport-events?sportType=ACTIVE
GET http://localhost:8090/api/sport-events?eventStatus=FOOTBALL
GET http://localhost:8090/api/sport-events?eventStatus=ACTIVE&sportType=FOOTBALL

POST http://localhost:8090/api/sport-event
Content-Type: application/json
{
  "name": "event-1",
  "type": "FOOTBALL",
  "status": "INACTIVE",
  "startTime": "2024-11-17T16:43:27"
}

PATCH http://localhost:8090/api/sport-events/1/status?newStatus=INACTIVE

