# Springboot with Kafka

## Why

Just to have a template of SpringBoot with Kafka for ready reference.

## What

```androiddatabinding
User -- uploads --> Uploader Service(short computation) -- saves
                           |                     |           |
                           |                   creates       |
                        [message]                |           |
                           |                     v           v
                           |                  Metadata     File
                           v                     ^           |
                          Kafka                  |           |
                           |                   updates    parses
                           v                     |           |
                       Parser Service(long computation) <----+
```

## How

```
$ docker-compose -f docker-compose-app.yml up

$ http -f POST :9090/api/v1/files file@~/data.txt
HTTP/1.1 201
Connection: keep-alive
Content-Length: 0
Date: Tue, 04 Jan 2022 17:14:54 GMT
Keep-Alive: timeout=60
Location: http://localhost:9090/api/v1/files/24

$ http :9090/api/v1/files/24
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Tue, 04 Jan 2022 17:15:44 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "id": 24,
    "letterCount": null,
    "name": "data.txt",
    "uuid": "2801eb6c-80e0-4bd8-9dd6-3d53b3596ead",
    "wordCount": null
}

# ---------- After the event is processed ----------
# ---- we get the values of letter/word counts -----

$ http :9090/api/v1/files/24
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Tue, 04 Jan 2022 17:15:44 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "id": 24,
    "letterCount": 57,
    "name": "data.txt",
    "uuid": "2801eb6c-80e0-4bd8-9dd6-3d53b3596ead",
    "wordCount": 11
}
```

## Dev

For all projects, in the Intellij IDEA, `Run > Edit Configurations > Environment Variables` put:

```
spring.profiles.active=dev
```

Then, you need to run the infra docker-compose, which will just make db, zookeeper and kafka up.

```
$ docker-compose -f docker-compose-infra.yml up
```

Then, you can run all the projects in debug.
