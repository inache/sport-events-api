Sport events api.<br/>
 This is what i've managed to make in two days after working hours ( there are things which can be improved like: better startTime validation / integration tests / validation tests . (To think about what can be improved for a lot of calls for 1 endpoint) <br/>
but been limited in time ).

Clone repo to local machine.<br/>
Inside project dir run:
mvn spring-boot:run

java.version:17

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

