Sport events api.
  This is what i've managed to make in two days after working hours ( there are things which can be improved like: better startTime validation / integration tests / validation tests . (To think about what can be improved for a lot of calls for 1 endpoint) <br/>
but been limited in time ).

java.version:17

To run the application:<br/>
mvn clean install => mvn spring-boot:run

Endpoints examples:<br/>
GET http://localhost:8090/api/sport-events<br/>
GET http://localhost:8090/api/sport-events/1<br/>
GET http://localhost:8090/api/sport-events?sportType=ACTIVE<br/>
GET http://localhost:8090/api/sport-events?eventStatus=FOOTBALL<br/>
GET http://localhost:8090/api/sport-events?eventStatus=ACTIVE&sportType=FOOTBALL<br/>

POST http://localhost:8090/api/sport-event<br/>
Content-Type: application/json<br/>
  {<br/>
    "name": "event-1",<br/>
    "type": "FOOTBALL",<br/>
    "status": "INACTIVE",<br/>
    "startTime": "2025-11-17T16:43:27"<br/>
  }

PATCH http://localhost:8090/api/sport-events/1/status?newStatus=INACTIVE

